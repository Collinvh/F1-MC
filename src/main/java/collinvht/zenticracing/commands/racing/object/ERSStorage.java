package collinvht.zenticracing.commands.racing.object;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.listener.VPPListener;
import collinvht.zenticracing.listener.vehicle.VehicleUtil;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
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
                        switch (ERSMODE) {
                            case 0:
                                ersCount = Math.min(100, ersCount + 1);
                            case 1:
                                ersCount = Math.max(0, ersCount - 1);
                            case 2:
                                ersCount = Math.max(0, ersCount - 2);
                            case 3:
                                ersCount = Math.max(0, ersCount - 4);
                        }
                    } else {
                        ersCount = Math.max(100, ersCount + 1);
                    }
                    VehicleUtil util = VPPListener.getUtil().get(car.getSpawnedVehicle().getStorageVehicle().getUuid());
                    if(util != null) {
                        switch (ERSMODE) {
                            case 0:
                                util.removeMaxSpeed(6);
                                break;
                            case 1:
                                util.removeMaxSpeed(4);
                                break;
                            case 3:
                                util.addMaxSpeed(2);
                                break;
                        }

                        switch (FMMode) {
                            case 0:
                                util.removeMaxSpeed(5);
                                break;
                            case 1:
                                util.removeMaxSpeed(3);
                                break;
                            case 2:
                                util.removeMaxSpeed(0);
                                break;
                            case 3:
                                util.addMaxSpeed(2);
                                break;
                        }
                    } else {
                        util = new VehicleUtil(car.getSpawnedVehicle());
                        VPPListener.getUtil().put(car.getSpawnedVehicle().getStorageVehicle().getUuid(), util);
                    }

                } else {
                    Bukkit.getScheduler().cancelTask(taskId);
                }
            }
        }.runTaskTimer(ZenticRacing.getRacing(), 0, 20).getTaskId();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
