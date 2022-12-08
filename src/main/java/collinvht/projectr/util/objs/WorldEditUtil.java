package collinvht.projectr.util.objs;

import collinvht.projectr.ProjectR;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import lombok.Getter;
import org.bukkit.Bukkit;

public class WorldEditUtil {
    @Getter
    private static WorldEditPlugin worldEdit;

    public static void init(ProjectR racing) {
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(worldEdit == null) {
            Bukkit.getPluginManager().disablePlugin(racing);
        }
    }

    public static Player getPlayer(org.bukkit.entity.Player player) {
        return BukkitAdapter.adapt(player);
    }

    public static LocalSession getSession(org.bukkit.entity.Player player) {
        return worldEdit.getSession(player);
    }
}
