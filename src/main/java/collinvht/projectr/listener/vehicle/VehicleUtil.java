package collinvht.projectr.listener.vehicle;

import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;

public class VehicleUtil {

    @Getter @Setter
    private int maxSpeed;

    @Getter @Setter
    private int fuelUsage;

    @Getter @Setter
    private SpawnedVehicle vehicle;


    public VehicleUtil(SpawnedVehicle vehicle) {
        this.vehicle = vehicle;
    }
}
