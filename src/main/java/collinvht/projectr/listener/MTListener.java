package collinvht.projectr.listener;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.objects.race.RaceDriver;
import lombok.Getter;
import nl.mtvehicles.core.events.VehicleEnterEvent;
import nl.mtvehicles.core.events.VehicleLeaveEvent;
import nl.mtvehicles.core.infrastructure.models.Vehicle;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class MTListener implements Listener {
    @Getter
    private static final HashMap<UUID, RaceDriver> raceDrivers = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void EnterVehicle(VehicleEnterEvent event) {
        Vehicle vehicle = event.getVehicle();
        if(event.getPlayer() != null) {
            RaceDriver driver = raceDrivers.get(event.getPlayer().getUniqueId());
            if (driver == null) {
                driver = new RaceDriver(event.getPlayer().getUniqueId());
                raceDrivers.put(event.getPlayer().getUniqueId(), driver);
            }

            driver.setVehicle(vehicle);
            driver.setDriving(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void OnVehicleLeave(VehicleLeaveEvent event) {
        if(event.getPlayer() != null) {
            if (containsDriver(event.getPlayer().getUniqueId())) {
                RaceDriver driver = raceDrivers.get(event.getPlayer().getUniqueId());
                driver.setDriving(false);
                driver.setVehicle(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void OnPlayerLeave(PlayerQuitEvent event) {
        if(containsDriver(event.getPlayer().getUniqueId())) {
            RaceDriver driver = raceDrivers.get(event.getPlayer().getUniqueId());
            driver.setDriving(false);
            driver.setVehicle(null);
        }
    }

    private static boolean containsDriver(UUID uuid) {
        return raceDrivers.containsKey(uuid);
    }

    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new MTListener(), ProjectR.getInstance());
    }

    public static void disable() {

    }
}
