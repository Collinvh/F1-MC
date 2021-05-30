package collinvht.zenticracing.listener;

import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleEnterEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.sql.Driver;
import java.util.Objects;

public class VPPListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleEnterEvent(VehicleEnterEvent event) {
        Player player = event.getDriver();
        if(player != null) {
            DriverObject object = DriverManager.getDriver(player.getUniqueId());
            if(object != null) {
                object.setDriving(true);
                DriverManager.createScoreboard(object);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleLeaveEvent(VehicleLeaveEvent event) {
        Player player = event.getDriver();
        if(player != null) {
            DriverObject object = DriverManager.getDriver(player.getUniqueId());
            if(object != null) {
                object.setDriving(false);

                if (DriverManager.getTaskIds().get(object.getPlayerUUID()) != null) {
                    int id = DriverManager.getTaskIds().get(object.getPlayerUUID());
                    Bukkit.getScheduler().cancelTask(id);
                }

                object.getPlayer().setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
            }
        }
    }
}
