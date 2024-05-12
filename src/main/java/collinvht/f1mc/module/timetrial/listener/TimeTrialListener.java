package collinvht.f1mc.module.timetrial.listener;

import collinvht.f1mc.module.timetrial.manager.TimeTrialManager;
import collinvht.f1mc.util.modules.ListenerModuleBase;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TimeTrialListener extends ListenerModuleBase implements Listener {
    @Override
    public void load() {
        registerListener(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void vehicleDestroyEvent(VehicleLeaveEvent event) {
        TimeTrialManager.disablePlayer(event.getDriver(), false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void vehicleDestroyEvent(PlayerQuitEvent event) {
        TimeTrialManager.disablePlayer(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void serverJoinEvent(PlayerJoinEvent event) {
        TimeTrialManager.hideAllVehicles(event.getPlayer());
    }
}
