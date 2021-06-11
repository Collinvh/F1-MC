package collinvht.zenticracing.commands.racing.setup.obj;

import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.VehicleStats;
import org.bukkit.Bukkit;

import java.util.UUID;

public class SetupOBJ {

    @Getter
    private final UUID linkedPlayer;

    @Getter
    private final LimitedInteger frontWingAngle = new LimitedInteger(1, 11);
    @Getter
    private final LimitedInteger rearWingAngle = new LimitedInteger(1, 11);

    @Getter
    private final LimitedFloat frontCamber = new LimitedFloat(-3.5F, -2.5F);
    @Getter
    private final LimitedFloat rearCamber = new LimitedFloat(-2F, -1F);

    @Getter
    private final LimitedFloat frontToe = new LimitedFloat(0.05F,  0.15F);
    @Getter
    private final LimitedFloat rearToe = new LimitedFloat(0.2F, 0.50F);

    @Getter
    private final LimitedInteger frontRideHeight = new LimitedInteger(1, 11);
    @Getter
    private final LimitedInteger rearRideHeight = new LimitedInteger(1, 11);
    @Getter
    private final LimitedInteger brakePressure = new LimitedInteger(50, 100);
    @Getter
    private final LimitedInteger brakeBias = new LimitedInteger(50, 70);

    public SetupOBJ(UUID uuid) {
        linkedPlayer = uuid;
    }

    public void updateCar(SpawnedVehicle vehicle) {
        VehicleStats stats = vehicle.getStorageVehicle().getVehicleStats();

        if(stats != null) {
            int topspeed = vehicle.getBaseVehicle().getSpeedSettings().getBase();

            float angleCombo = frontWingAngle.getInteger() + rearWingAngle.getInteger();
            float rideCombo = frontRideHeight.getInteger() + rearRideHeight.getInteger();
            float camberCombo = frontCamber.getAFloat() + rearCamber.getAFloat();
            float toeCombo = frontToe.getAFloat() + rearToe.getAFloat();

            float limit = frontWingAngle.getTopLimit() + rearWingAngle.getTopLimit();

            float percentage = ((angleCombo*2) / limit) * 100;

            percentage = percentage / rideCombo;

            percentage *= toeCombo;
            percentage += camberCombo;



            topspeed *= ((100 - percentage) / 150);
            topspeed += 100 - (percentage * 4);

            Bukkit.getLogger().warning(String.valueOf(percentage));

            stats.setSteering(1 + (int) (50 * (percentage / 200)));
            stats.setSpeed(topspeed);
        }
    }
}
