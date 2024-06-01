package collinvht.f1mc.module.vehiclesplus.listener.listeners;

import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.module.racing.object.race.RaceCar;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleDestroyEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleEnterEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class VPListener implements Listener {
    @Getter
    private static final HashMap<UUID, RaceDriver> RACE_DRIVERS = new HashMap<>();
    @Getter
    private static final HashMap<UUID, RaceCar> RACE_CARS = new HashMap<>();
    private static final ArrayList<SpawnedVehicle> spawnedVehicles = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleEnterEvent(@NotNull VehicleEnterEvent event) {
        Player player = event.getDriver();
        SpawnedVehicle vehicle = (SpawnedVehicle) event.getVehicle();
        if(spawnedVehicles.contains(vehicle)) return;
        if(player != null && vehicle != null) {
            RaceDriver driver = RACE_DRIVERS.get(event.getDriver().getUniqueId());
            if (driver == null) {
                driver = new RaceDriver(event.getDriver());
                if(RACE_CARS.get(vehicle.getHolder().getUniqueId()) != null) {
                    RACE_CARS.get(vehicle.getHolder().getUniqueId()).setPlayer(driver);
                    driver.setRaceCar(RACE_CARS.get(vehicle.getHolder().getUniqueId()));
                }
                spawnedVehicles.add(vehicle);
                driver.setVehicle(vehicle);
                driver.setDriving(true);
                RACE_DRIVERS.put(event.getDriver().getUniqueId(), driver);
                return;
            }
            if(RACE_CARS.get(vehicle.getHolder().getUniqueId()) != null) {
                RACE_CARS.get(vehicle.getHolder().getUniqueId()).setPlayer(driver);
            }
            driver.setVehicle(vehicle);
            driver.setDriving(true);
            spawnedVehicles.add(vehicle);
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
                driver.setRaceCar(null);
            }
            SpawnedVehicle vehicle = (SpawnedVehicle) event.getVehicle();
            if(RACE_CARS.containsKey(vehicle.getHolder().getUniqueId())) {
                RACE_CARS.get(vehicle.getHolder().getUniqueId()).setPlayer(null);
            }
            spawnedVehicles.remove((SpawnedVehicle) event.getVehicle());
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
                NBTItem item = car.getRaceCarGUI().getTyre();
                if(item != null) {
                    car.getLinkedVehicle().getHolder().getLocation();
                    if (TyreManager.isTyre(item.getItem())) {
                        Item itemEntity = car.getLinkedVehicle().getHolder().getWorld().spawn(car.getLinkedVehicle().getHolder().getLocation(), Item.class);
                        itemEntity.setItemStack(item.getItem());
                    }
                }
                if(car.getPlayer() != null) {
                    car.getPlayer().setRaceCar(null);
                }
                RACE_CARS.remove(vehicle.getHolder().getUniqueId());
                spawnedVehicles.remove(vehicle);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleDestroyEvent(@NotNull PlayerQuitEvent event) {
        RaceDriver raceDriver = getRACE_DRIVERS().get(event.getPlayer().getUniqueId());
        if(raceDriver != null) {
            raceDriver.setDriving(false);
            raceDriver.setVehicle(null);
            raceDriver.setRaceCar(null);
        }
    }
}
