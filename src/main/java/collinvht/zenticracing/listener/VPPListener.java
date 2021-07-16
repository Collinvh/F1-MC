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
import collinvht.zenticracing.manager.tyre.TyreManager;
import collinvht.zenticracing.util.objs.VehicleWPlayer;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleDestroyEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleEnterEvent;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VPPListener implements Listener {
    @Getter
    private static final HashMap<String, VehicleUtil> util = new HashMap<>();
    private static final HashMap<String, VehicleWPlayer> vehicles = new HashMap<String, VehicleWPlayer>();
    private static int taskID;
    private static final Random rng = new Random();

    public static void cancel() {
        Bukkit.getScheduler().cancelTask(taskID);

        TyreManager.getTimers().forEach((taskID, integer) -> {
            Bukkit.getScheduler().cancelTask(integer);
        });
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
                if(object1 != null) {
                    RaceCar car = object1.getRaceCarFromVehicle(event.getSeat().getOwningVehicle());
                    if(car != null) {
                        car.setDriverObject(object);
                        car.getStorage().start();
                        car.setDriverObject(object);
                        object.setVehicle(car);

                        TyreManager.startTimer(car);
                    }
                }
                object.setCurvehicle(event.getSeat().getOwningVehicle());

                VehicleWPlayer vehicleWPlayer = new VehicleWPlayer();
                vehicleWPlayer.setVehicle(event.getSeat().getOwningVehicle());
                vehicleWPlayer.setPlayer(event.getDriver());

                vehicles.put(player.getUniqueId().toString(), vehicleWPlayer);

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
            VehicleWPlayer vehicle = vehicles.get(player.getUniqueId().toString());
            if(vehicle.getVehicle() != null) {
                DriverObject object = DriverManager.getDriver(player.getUniqueId());
                if (object != null) {
                    object.setDriving(false);

                    if (DriverManager.getTaskIds().get(object.getPlayerUUID()) != null) {
                        int id = DriverManager.getTaskIds().get(object.getPlayerUUID());
                        Bukkit.getScheduler().cancelTask(id);
                    }



                    TeamObject object1 = Team.checkTeamForPlayer(player);
                    if (object1 != null) {
                        RaceCar car = object1.getRaceCarFromVehicle(event.getSeat().getOwningVehicle());
                        if (car != null) {
                            car.setDriverObject(null);
                            object.setVehicle(car);
                        }
                    }

                    object.setCurvehicle(null);
                    TyreManager.stopTimer(vehicle.getVehicle());
                    vehicles.remove(player.getUniqueId().toString());
                    object.getPlayer().setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void vehicleDespawnEvent(VehicleDestroyEvent event) {

        if(event.getVehicle() != null) {
            SpawnedVehicle vehicle = event.getVehicle();
            Team.getTeamObj().forEach((s, teamObject) -> {
                RaceCar v = teamObject.getRaceCarFromVehicle(vehicle);
                if (v != null) {
                    if (v.getSpawnedVehicle() == vehicle) {
                        ItemStack stack = v.getBandGui().getItem(13);
                        Location location = vehicle.getHolder().getLocation();
                        if(location.getWorld() != null && stack != null) {
                            Item item = (Item) location.getWorld().spawnEntity(location, EntityType.DROPPED_ITEM);
                            item.setItemStack(stack);
                        }


                        v.resetStorage();
                        v.setSpawnedVehicle(null);
                        teamObject.getRaceCars().remove(v);
                    }
                }
            });
        }
    }

    static {
        taskID = new BukkitRunnable() {
            @Override
            public void run() {
                vehicles.forEach((s, vehicle) -> {
                    Player player = vehicle.getPlayer();
                    SetupOBJ obj = SetupManager.getSetup(player.getUniqueId());

                    if(vehicle.getVehicle().getBaseVehicle().getName().toLowerCase().contains("f1")) {
                        TeamObject object = Team.checkTeamForPlayer(player);
                        if(object != null) {
                            RaceCar car = object.getRaceCarFromVehicle(vehicle.getVehicle());
                            if(car != null) {
                                if (obj != null) {
                                    obj.updateCar(player, vehicle.getVehicle(), car);
                                } else {
                                    vehicle.getVehicle().getStorageVehicle().getVehicleStats().setSpeed(0);
                                }
                            } else {
                                vehicle.getVehicle().getStorageVehicle().getVehicleStats().setSpeed(0);
                            }
                        } else {
                            vehicle.getVehicle().getStorageVehicle().getVehicleStats().setSpeed(0);
                        }
                    } else {
                        int baseSpeed = vehicle.getVehicle().getBaseVehicle().getSpeedSettings().getBase();
                        Block block = player.getWorld().getBlockAt(player.getLocation());
                        switch (block.getType()) {
                            case GREEN_CONCRETE_POWDER:
                            case LIME_CONCRETE_POWDER:
                            case GRASS_BLOCK:
                                baseSpeed *= 0.6F;
                                break;
                            case ANDESITE:
                            case LIGHT_GRAY_CONCRETE_POWDER:
                            case LIGHT_GRAY_CONCRETE:
                            case COBBLESTONE:
                            case GRAVEL:
                                baseSpeed *= 0.4F;
                                break;
                            case TERRACOTTA:
                            case STONE:
                                baseSpeed *= 0.75F;
                                break;
                            case GRAY_CONCRETE_POWDER:
                                baseSpeed = 60;
                                break;
                        }
                        vehicle.getVehicle().getStorageVehicle().getVehicleStats().setSpeed(baseSpeed);
                    }


                    if(vehicle.getVehicle().getCurrentSpeedInKm() > vehicle.getVehicle().getStorageVehicle().getVehicleStats().getSpeed()) {
                        vehicle.getVehicle().getStorageVehicle().getVehicleStats().setCurrentSpeed(Double.valueOf(vehicle.getVehicle().getStorageVehicle().getVehicleStats().getSpeed()));
                    }
                });
            }
        }.runTaskTimer(ZenticRacing.getRacing(), 0, 1).getTaskId();
    }
}
