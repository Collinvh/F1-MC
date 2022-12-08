package collinvht.projectr;

import collinvht.projectr.commands.Commands;
import collinvht.projectr.commands.racing.setup.SetupManager;
import collinvht.projectr.commands.team.Team;
import collinvht.projectr.listener.Listeners;
import collinvht.projectr.listener.VPPListener;
import collinvht.projectr.util.ConfigUtil;
import collinvht.projectr.util.Utils;
import collinvht.projectr.util.objs.DiscordUtil;
import collinvht.projectr.util.objs.JSONUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProjectR extends JavaPlugin {
    @Getter
    private static ProjectR racing;
    @Getter
    private static final String serverPrefix = "" + ChatColor.YELLOW + ChatColor.BOLD + "R > " + ChatColor.RESET;

    @Override
    public void onEnable() {
        racing = this;

        Utils.initializeUtils();
        Commands.initializeCommands();
        Listeners.initializeListeners();

        for(Player player : Bukkit.getOnlinePlayers()) {
            SetupManager.createSetupForPlayer(player.getUniqueId());
        }

        JSONUtil.load();

        ConfigUtil.loadConfig();
    }

    @Override
    public void onDisable() {
        DiscordUtil.close();
        JSONUtil.unload();
        Team.getTeamObj().forEach((s, teamObject) -> teamObject.getRaceCars().forEach(car -> {
            if(car.getSpawnedVehicle() != null) {
                ItemStack stack = car.getBandGui().getItem(13);
                Location location = car.getSpawnedVehicle().getHolder().getLocation();
                if(location.getWorld() != null && stack != null) {
                    Item item = (Item) location.getWorld().spawnEntity(location, EntityType.DROPPED_ITEM);
                    item.setItemStack(stack);
                }

                car.getSpawnedVehicle().despawn(true);
            }
        }));
        VPPListener.cancel();

        ConfigUtil.save();
    }
}
