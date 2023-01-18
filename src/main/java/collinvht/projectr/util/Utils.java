package collinvht.projectr.util;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.objects.race.RaceDriver;
import com.google.gson.*;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
    private static LuckPerms luckPerms;
    private static WorldEditPlugin worldEdit;

    /*
    Dependant Plugins

    Most tasks won't run without this that's why the plugin disables if
    the plugin isn't present
     */
    public static LuckPerms getLuckperms() {
        if(luckPerms != null) return luckPerms;
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            return luckPerms;
        } else {
            Bukkit.getLogger().severe("LUCK PERMS NOT PRESENT DISABLING");
            Bukkit.getPluginManager().disablePlugin(ProjectR.getInstance());
        }
        return null;
    }

    public static WorldEditPlugin getWorldEdit() {
        if(worldEdit != null) return worldEdit;
        RegisteredServiceProvider<WorldEditPlugin> provider = Bukkit.getServicesManager().getRegistration(WorldEditPlugin.class);
        if (provider != null) {
            worldEdit = provider.getProvider();
            return worldEdit;
        } else {
            Bukkit.getLogger().severe("WORLD EDIT NOT PRESENT DISABLING");
            Bukkit.getPluginManager().disablePlugin(ProjectR.getInstance());
        }
        return null;
    }
    /*
    This creates a location from a WorldEdit region
     */

    public static Location blockVectorToLocation(World world, BlockVector3 vector3) {
        return new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static com.sk89q.worldedit.world.World getAdaptedWorld(World world) {
        return BukkitAdapter.adapt(world);
    }

    public static LocalSession getSession(org.bukkit.entity.Player player) {
        return worldEdit.getSession(player);
    }

    /*
    Formats milliseconds based on minutes seconds and milliseconds
     */
    public static String millisToTimeString(final long mSec) {
        final String pattern = "mm:ss.SSS";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(mSec));
    }
    /*
    Sorts a map descending. Used for fastest laps finish positions ect.
     */
    public static LinkedHashMap<RaceDriver, Long> sortByValueDesc(Map<RaceDriver, Long> map) {
        List<Map.Entry<RaceDriver, Long>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Collections.reverse(list);

        LinkedHashMap<RaceDriver, Long> result = new LinkedHashMap<>();
        for (Map.Entry<RaceDriver, Long> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    /*
    Reads a json file and returns it as a JsonElement
     */

    public static JsonElement readJson(String filename) throws Exception {
        return JsonParser.parseReader(new FileReader(filename));
    }

    /*
    Saves the json file to a certain path
     */
    public static void saveJSON(File path, String name, JsonObject object) {
        try {
            if(!path.mkdirs()) {
                Files.createDirectories(Paths.get(path.toURI()));
            }
            FileWriter writer = new FileWriter(path + "/" + name + ".json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(object));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
