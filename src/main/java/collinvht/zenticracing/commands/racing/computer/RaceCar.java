package collinvht.zenticracing.commands.racing.computer;

import collinvht.zenticracing.commands.racing.object.ERSStorage;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;

public class RaceCar {

    @Getter
    private final SpawnedVehicle spawnedVehicle;

    @Getter
    private ERSStorage storage;


    @Getter @Setter
    private DriverObject driverObject;


    public RaceCar(SpawnedVehicle spawnedVehicle) {
        this.spawnedVehicle = spawnedVehicle;
        storage = new ERSStorage(this);
    }

    public void resetStorage() {
        storage.stop();
        storage = new ERSStorage(this);
    }
}
