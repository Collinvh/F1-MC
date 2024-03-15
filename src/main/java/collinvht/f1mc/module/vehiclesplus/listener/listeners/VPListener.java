package collinvht.f1mc.module.vehiclesplus.listener.listeners;

import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleEnterEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class VPListener implements Listener {
    @Getter
    private static final HashMap<UUID, RaceDriver> RACE_DRIVERS = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleEnterEvent(@NotNull VehicleEnterEvent event) {
        Player player = event.getDriver();
        SpawnedVehicle vehicle = (SpawnedVehicle) event.getVehicle();
        if(player != null && vehicle != null) {
            RaceDriver driver = RACE_DRIVERS.get(event.getDriver().getUniqueId());
            if (driver == null) {
                driver = new RaceDriver(event.getDriver().getUniqueId());
                RACE_DRIVERS.put(event.getDriver().getUniqueId(), driver);
            }
            driver.setDriving(true);
            driver.setVehicle(vehicle);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleLeaveEvent(@NotNull VehicleLeaveEvent event) {
        Player player = event.getDriver();
        if(player != null) {
            RaceDriver driver = RACE_DRIVERS.get(player.getUniqueId());
            if(driver != null) {
                driver.setVehicle(null);
                driver.setDriving(false);
            }
        }
    }
}
