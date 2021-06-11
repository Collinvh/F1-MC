package collinvht.zenticracing.listener;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.commands.racing.setup.SetupManager;
import collinvht.zenticracing.commands.racing.setup.obj.SetupOBJ;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.object.TeamObject;
import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import collinvht.zenticracing.listener.vehicle.VehicleUtil;
import collinvht.zenticracing.util.objs.VehicleWPlayer;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleDestroyEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleEnterEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class VPPListener implements Listener {
    @Getter
    private static final HashMap<String, VehicleUtil> util = new HashMap<>();
    private static final HashMap<String, VehicleWPlayer> vehicles = new HashMap<String, VehicleWPlayer>();
    private static int taskID;

    public static void cancel() {
        Bukkit.getScheduler().cancelTask(taskID);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleEnterEvent(VehicleEnterEvent event) {
        Player player = event.getDriver();
        if(player != null) {
            DriverObject object = DriverManager.getDriver(player.getUniqueId());
            if(object != null) {
                object.setDriving(true);
                DriverManager.createScoreboard(object);

                TeamObject object1 = Team.checkTeamForPlayer(player);
//                if(object1 != null) {
//                    RaceCar car = object1.getRaceCarFromVehicle(event.getSeat().getOwningVehicle());
//                    if(car != null) {
//                        car.setDriverObject(object);
//                        car.getStorage().start();
//                        object.setVehicle(car);
//                    }
//                }
                object.setCurvehicle(event.getSeat().getOwningVehicle());

                VehicleWPlayer vehicleWPlayer = new VehicleWPlayer();
                vehicleWPlayer.setVehicle(event.getSeat().getOwningVehicle());
                vehicleWPlayer.setPlayer(event.getDriver());

                vehicles.put(event.getSeat().getOwningVehicle().getStorageVehicle().getUuid(), vehicleWPlayer);

                if(util.get(event.getSeat().getOwningVehicle().getStorageVehicle().getUuid()) == null) {
                    util.put(event.getSeat().getOwningVehicle().getStorageVehicle().getUuid(), new VehicleUtil(event.getSeat().getOwningVehicle()));
                }
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

//                TeamObject object1 = Team.checkTeamForPlayer(player);
//                if(object1 != null) {
//                    RaceCar car = object1.getRaceCarFromVehicle(event.getSeat().getOwningVehicle());
//                    if(car != null) {
//                        car.setDriverObject(null);
//                        object.setVehicle(car);
//                    }
//                }

                object.setCurvehicle(null);
                vehicles.remove(event.getSeat().getOwningVehicle().getStorageVehicle().getUuid());
                object.getPlayer().setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleDespawnEvent(VehicleDestroyEvent event) {
        SpawnedVehicle vehicle = (SpawnedVehicle) event.getVehicle();
        Team.getTeamObj().forEach((s, teamObject) -> {
            RaceCar v = teamObject.getRaceCarFromVehicle(vehicle);
            if(v != null) {
                if(v.getSpawnedVehicle() == vehicle) {
                    teamObject.getRaceCars().remove(v);
                }
            }
        });
    }

    static {
        taskID = new BukkitRunnable() {
            @Override
            public void run() {
                vehicles.forEach((s, vehicle) -> {
                    Player player = vehicle.getPlayer();
                    SetupOBJ obj = SetupManager.getSetup(player.getUniqueId());

                    if(vehicle.getVehicle().getBaseVehicle().getName().toLowerCase().contains("f1")) {
                        if (obj != null) {
                            obj.updateCar(vehicle.getVehicle());
                        }
                    }

                    if(vehicle.getVehicle().getCurrentSpeedInKm() > 1) {
                        int curspeed = vehicle.getVehicle().getCurrentSpeedInKm();
                        int maxspeed = vehicle.getVehicle().getStorageVehicle().getVehicleStats().getSpeed();
                        if (curspeed > maxspeed) {
                            vehicle.getVehicle().getStorageVehicle().getVehicleStats().setCurrentSpeed((double) maxspeed);
                        } else {
//                            vehicle.getVehicle().getStorageVehicle().getVehicleStats().setCurrentSpeed((double) curspeed);
                        }
                    }
                });
//                util.forEach((s, vehicleUtil) -> {
//                    SpawnedVehicle vehicle = vehicleUtil.getVehicle();
//
//                    VehicleStats stats = vehicle.getStorageVehicle().getVehicleStats();
//
//                    stats.setSpeed(vehicleUtil.getMaxSpeed());
//
//
//                    vehicle.getStorageVehicle().setVehicleStats(stats);
//                });
            }
        }.runTaskTimer(ZenticRacing.getRacing(), 0, 1).getTaskId();
    }
}
