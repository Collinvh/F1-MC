package collinvht.projectr.module.main.listener.listeners;

import collinvht.projectr.module.main.objects.RaceDriver;
import lombok.Getter;
import nl.sbdeveloper.vehiclesplus.api.events.impl.VehicleDespawnEvent;
import nl.sbdeveloper.vehiclesplus.api.events.impl.VehicleDestroyEvent;
import nl.sbdeveloper.vehiclesplus.api.events.impl.VehicleEnterEvent;
import nl.sbdeveloper.vehiclesplus.api.events.impl.VehicleLeaveEvent;
import nl.sbdeveloper.vehiclesplus.api.vehicles.impl.SpawnedVehicle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainVehicleListener implements Listener {
    @Getter
    private static final HashMap<UUID, RaceDriver> RACE_DRIVERS = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleEnterEvent(@NotNull VehicleEnterEvent event) {
        Player player = event.getPlayer();
        SpawnedVehicle vehicle = event.getVehicle();
        if(player != null && vehicle != null) {
            RaceDriver driver = RACE_DRIVERS.get(event.getPlayer().getUniqueId());
            if (driver == null) {
                driver = new RaceDriver(event.getPlayer().getUniqueId());
                RACE_DRIVERS.put(event.getPlayer().getUniqueId(), driver);
            }
            driver.setDriving(true);
            driver.setVehicle(vehicle);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleLeaveEvent(@NotNull VehicleLeaveEvent event) {
        Player player = event.getPlayer();
        if(player != null) {
            RaceDriver driver = RACE_DRIVERS.get(player.getUniqueId());
            if(driver != null) {
                driver.setVehicle(null);
                driver.setDriving(false);
            }
        }
    }

    @EventHandler
    public static void vehiclePickUpEvent(@NotNull VehicleDespawnEvent event) {
    }

    @EventHandler
    public static void vehicleDeleteEvent(@NotNull VehicleDestroyEvent event) {
    }
}
