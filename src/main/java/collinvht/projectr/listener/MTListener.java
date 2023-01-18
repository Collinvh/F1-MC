package collinvht.projectr.listener;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.TukTukCommand;
import collinvht.projectr.manager.vehicle.SetupManager;
import collinvht.projectr.manager.vehicle.SlowDownManager;
import collinvht.projectr.util.objects.race.RaceDriver;
import lombok.Getter;
import nl.mtvehicles.core.events.VehicleEnterEvent;
import nl.mtvehicles.core.events.VehicleLeaveEvent;
import nl.mtvehicles.core.infrastructure.dataconfig.VehicleDataConfig;
import nl.mtvehicles.core.infrastructure.helpers.VehicleData;
import nl.mtvehicles.core.infrastructure.models.Vehicle;
import nl.mtvehicles.core.infrastructure.models.VehicleUtils;
import nl.mtvehicles.core.infrastructure.modules.ConfigModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class MTListener implements Listener {
    @Getter
    private static final HashMap<UUID, RaceDriver> raceDrivers = new HashMap<>();

    private static int vehicleRunnable;

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void EnterVehicle(VehicleEnterEvent event) {
        Vehicle vehicle = event.getVehicle();
        if(event.getPlayer() != null) {
            RaceDriver driver = raceDrivers.get(event.getPlayer().getUniqueId());
            if (driver == null) {
                driver = new RaceDriver(event.getPlayer().getUniqueId());
                raceDrivers.put(event.getPlayer().getUniqueId(), driver);
            }
            if(((double)ConfigModule.vehicleDataConfig.get(event.getLicensePlate(), VehicleDataConfig.Option.DOWNFORCE)) > 1.0) {
                double downforce = SetupManager.getSetup(event.getPlayer().getUniqueId()).getDownForceFromSettings();
                VehicleData.Downforce.put(vehicle.getLicensePlate(), downforce);
            }

            driver.setVehicle(vehicle);
            driver.setDriving(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void OnVehicleLeave(VehicleLeaveEvent event) {
        if(event.getPlayer() != null) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            if (containsDriver(uuid)) {
                RaceDriver driver = raceDrivers.get(event.getPlayer().getUniqueId());
                driver.setDriving(false);
                driver.setVehicle(null);
            }
            if(TukTukCommand.getTuktuks().containsKey(uuid)) {
                String plate = TukTukCommand.getTuktuks().get(uuid);
                VehicleUtils.deleteVehicle(plate, player,false);
                TukTukCommand.getTuktuks().remove(uuid);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void OnPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if(containsDriver(uuid)) {
            RaceDriver driver = raceDrivers.get(uuid);
            driver.setDriving(false);
            driver.setVehicle(null);
        }
        if(TukTukCommand.getTuktuks().containsKey(uuid)) {
            String plate = TukTukCommand.getTuktuks().get(uuid);
            VehicleUtils.deleteVehicle(plate, player,false);
            TukTukCommand.getTuktuks().remove(uuid);
        }
    }

    private static boolean containsDriver(UUID uuid) {
        return raceDrivers.containsKey(uuid);
    }

    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new MTListener(), ProjectR.getInstance());
        vehicleRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                raceDrivers.forEach((uuid, raceDriver) -> {
                    if(raceDriver.getVehicle() != null) {
                        if(raceDriver.isDriving()) {
                            SlowDownManager.update(raceDriver);
                        }
                    }

                });
            }
        }.runTaskTimer(ProjectR.getInstance(), 0, 0).getTaskId();
    }

    public static void disable() {
        Bukkit.getScheduler().cancelTask(vehicleRunnable);
    }
}
