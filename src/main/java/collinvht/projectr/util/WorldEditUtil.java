package collinvht.projectr.util;

import collinvht.projectr.ProjectR;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;

public class WorldEditUtil {
    @Getter
    private static WorldEditPlugin worldEdit;

    public static void initialize() {
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(worldEdit == null) {
            Bukkit.getPluginManager().disablePlugin(ProjectR.getInstance());
        }
    }

    public static World getAdaptedWorld(org.bukkit.World world) {
        return BukkitAdapter.adapt(world);
    }

    public static LocalSession getSession(org.bukkit.entity.Player player) {
        return worldEdit.getSession(player);
    }
}