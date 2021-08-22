package collinvht.zenticracing;

import collinvht.zenticracing.commands.Commands;
import collinvht.zenticracing.commands.racing.setup.SetupManager;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.listener.Listeners;
import collinvht.zenticracing.listener.VPPListener;
import collinvht.zenticracing.util.ConfigUtil;
import collinvht.zenticracing.util.Utils;
import collinvht.zenticracing.util.objs.DiscordUtil;
import collinvht.zenticracing.util.objs.JSONUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZenticRacing extends JavaPlugin {
    @Getter
    private static ZenticRacing racing;

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

        //ConfigUtil.save();
    }
}
