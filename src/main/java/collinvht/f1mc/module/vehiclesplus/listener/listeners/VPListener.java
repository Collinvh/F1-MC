package collinvht.f1mc.module.vehiclesplus.listener.listeners;

import collinvht.f1mc.module.racing.object.race.RaceCar;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleDestroyEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleEnterEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class VPListener implements Listener {
    @Getter
    private static final HashMap<UUID, RaceDriver> RACE_DRIVERS = new HashMap<>();
    @Getter
    private static final HashMap<UUID, RaceCar> RACE_CARS = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleEnterEvent(@NotNull VehicleEnterEvent event) {
        Player player = event.getDriver();
        SpawnedVehicle vehicle = (SpawnedVehicle) event.getVehicle();
        if(player != null && vehicle != null) {
            RaceDriver driver = RACE_DRIVERS.get(event.getDriver().getUniqueId());
            if (driver == null) {
                driver = new RaceDriver(event.getDriver());
                driver.setDriving(true);
                driver.setVehicle(vehicle);
                if(RACE_CARS.get(vehicle.getHolder().getUniqueId()) != null) {
                    RACE_CARS.get(vehicle.getHolder().getUniqueId()).setPlayer(driver);
                }
                RACE_DRIVERS.put(event.getDriver().getUniqueId(), driver);
                return;
            }
            driver.setDriving(true);
            driver.setVehicle(vehicle);
            if(RACE_CARS.get(vehicle.getHolder().getUniqueId()) != null) {
                RACE_CARS.get(vehicle.getHolder().getUniqueId()).setPlayer(driver);
            }
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
            SpawnedVehicle vehicle = (SpawnedVehicle) event.getVehicle();
            if(RACE_CARS.containsKey(vehicle.getHolder().getUniqueId())) {
                RACE_CARS.get(vehicle.getHolder().getUniqueId()).setPlayer(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleDestroyEvent(@NotNull VehicleDestroyEvent event) {
        SpawnedVehicle vehicle = (SpawnedVehicle) event.getVehicle();
        if (vehicle != null) {
            RaceCar car = RACE_CARS.get(vehicle.getHolder().getUniqueId());
            if (car != null) {
                car.getLinkedTeam().getRaceCars().remove(car);
                car.getRaceCarGUI().stopTimer();
                RACE_CARS.remove(vehicle.getHolder().getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleDestroyEvent(@NotNull PlayerQuitEvent event) {
        getRACE_DRIVERS().remove(event.getPlayer().getUniqueId());
    }
}
