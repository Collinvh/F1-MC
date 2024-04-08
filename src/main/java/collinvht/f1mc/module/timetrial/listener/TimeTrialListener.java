package collinvht.f1mc.module.timetrial.listener;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.timetrial.command.TimeTrialManager;
import collinvht.f1mc.module.timetrial.obj.TimeTrialSession;
import collinvht.f1mc.util.modules.ListenerModuleBase;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TimeTrialListener extends ListenerModuleBase implements Listener {
    @Override
    public void load() {
        registerListener(this);
    }

    @EventHandler
    public static void vehicleDestroyEvent(VehicleLeaveEvent event) {
        TimeTrialSession session = TimeTrialManager.getSessionHashMap().get(event.getDriver().getUniqueId());
        if(session != null) {
            session.getSpawnedVehicle().getStorageVehicle().removeVehicle(event.getDriver());
            event.getDriver().teleport(session.getPrevLoc());
            TimeTrialManager.getSessionHashMap().remove(event.getDriver().getUniqueId());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.getUniqueId().equals(event.getDriver().getUniqueId())) {
                    onlinePlayer.showPlayer(F1MC.getInstance(), event.getDriver());
                    session.stop();
                }
            }
        }
    }

    @EventHandler
    public static void serverJoinEvent(PlayerJoinEvent event) {
        TimeTrialManager.getSessionHashMap().forEach((uuid, timeTrialSession) -> {
            Player player = Bukkit.getPlayer(timeTrialSession.getPlayer().getUniqueId());
            if(player == null) return;
            event.getPlayer().hidePlayer(F1MC.getInstance(), player);
            event.getPlayer().hideEntity(F1MC.getInstance(), timeTrialSession.getSpawnedVehicle().getHolder());
        });
    }
}
