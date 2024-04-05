package collinvht.f1mc.util;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import com.google.gson.*;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.checkerframework.checker.units.qual.A;
import tsp.headdb.core.api.HeadAPI;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/*
@author Collinvht
 */
public class Utils {
    private static LuckPerms luckPerms = null;
    private static WorldEditPlugin worldEdit = null;

    private static Gson gson;
    /*
    Dependant Plugins

    Most tasks won't run without this that's why the plugin disables if
    the plugin isn't present
     */
    public static LuckPerms getLuckperms() {
        if(luckPerms != null) return luckPerms;
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            Bukkit.getLogger().severe("LUCKPERMS NOT PRESENT DISABLING");
            Bukkit.getPluginManager().disablePlugin(F1MC.getInstance());
        }
        return luckPerms;
    }

    public static WorldEditPlugin getWorldEdit() {
        if(worldEdit != null) return worldEdit;
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit != null) {
            return worldEdit;
        } else {
            Bukkit.getLogger().severe("WORLD EDIT NOT PRESENT DISABLING");
            Bukkit.getPluginManager().disablePlugin(F1MC.getInstance());
        }
        return null;
    }
    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
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
        return getWorldEdit().getSession(player);
    }

    /*
    Formats milliseconds based on minutes seconds and milliseconds
     */
    public static String millisToTimeString(final long mSec) {
        return millisToTimeString(mSec, "mm:ss.SSS");
    }
    /*
    Formats milliseconds based on minutes seconds and milliseconds
     */
    public static String millisToTimeString(long mSec, String pattern) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        if(mSec < 0) {
            return  "+" + simpleDateFormat.format(new Date(-mSec));
        } else {
            if(pattern.equalsIgnoreCase("ss.SS")) {
                return "-" + simpleDateFormat.format(new Date(mSec));
            } else {
                return simpleDateFormat.format(new Date(mSec));
            }
        }
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

    public static ItemStack emptyStack(Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(" ");
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /*
    Saves the json file to a certain path
     */
    public static void saveJSON(File path, String name, JsonElement object) {
        try {
            if(!path.mkdirs()) {
                Files.createDirectories(Paths.get(path.toURI()));
            }
            FileWriter writer = new FileWriter(path + "/" + name + ".json");
            if(gson == null) gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(object));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack createSkull(int id, String name) {
        if(HeadAPI.getHeadById(id).isPresent()) {
            ItemStack stack = HeadAPI.getHeadById(id).get().getItem(UUID.randomUUID());
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                ArrayList<String> strings = new ArrayList<>();
                strings.add(ChatColor.DARK_GRAY + "Click to change countries!");
                meta.setLore(strings);
                stack.setItemMeta(meta);
            }
            return stack;
        } else return new ItemStack(Material.AIR);
    }
}
