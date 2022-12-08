package collinvht.projectr.commands.racing.object;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.racing.computer.RaceCar;
import collinvht.projectr.listener.VPPListener;
import collinvht.projectr.listener.vehicle.VehicleUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ERSStorage {

    @Getter @Setter
    private int ERSMODE = 0;
    @Getter @Setter
    private int FMMode = 0;

    @Getter @Setter
    private int ersCount = 100;

    private int taskId;

    private final RaceCar car;

    public ERSStorage(RaceCar car) {
        this.car = car;
    }



    public void start() {
        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if(car.getSpawnedVehicle() != null) {
                    if(car.getSpawnedVehicle().getCurrentSpeedInKm() > 50) {
                        if(ersCount >= 1) {
                            switch (ERSMODE) {
                                case 0:
                                    if(ersCount < 100) {
                                        ersCount += 1;
                                    } else if(ersCount > 100) {
                                        ersCount = 100;
                                    }
                                    break;
                                case 1:
                                    ersCount = Math.max(0, ersCount - 1);
                                    break;
                                case 2:
                                    ersCount = Math.max(0, ersCount - 2);
                                    break;
                                case 3:
                                    ersCount = Math.max(0, ersCount - 4);
                                    break;
                            }
                        } else {
                            setERSMODE(0);
                        }
                    } else {
                        if(ersCount < 100) {
                            ersCount += 1;
                        } else if(ersCount > 100) {
                            ersCount = 100;
                        }
                    }
                    VehicleUtil util = VPPListener.getUtil().get(car.getSpawnedVehicle().getStorageVehicle().getUuid());
                    if(util != null) {
                        int speed = 0;
                        switch (ERSMODE) {
                            case 0:
                                speed -= 3;
                                break;
                            case 1:
                                break;
                            case 3:
                                speed += 5;
                                break;
                        }

                        switch (FMMode) {
                            case 0:
                                speed -= 5;
                                util.setFuelUsage(0);
                                break;
                            case 1:
                                util.setFuelUsage(2);
                                break;
                            case 2:
                                speed += 5;
                                util.setFuelUsage(4);
                                break;
                            case 3:
                                speed += 10;
                                util.setFuelUsage(6);
                                break;
                        }

                        util.setMaxSpeed(speed);
                    } else {
                        util = new VehicleUtil(car.getSpawnedVehicle());
                        VPPListener.getUtil().put(car.getSpawnedVehicle().getStorageVehicle().getUuid(), util);
                    }

                } else {
                    Bukkit.getScheduler().cancelTask(taskId);
                }
            }
        }.runTaskTimer(ProjectR.getRacing(), 0, 20).getTaskId();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
