package collinvht.zenticracing.commands.team.object;

import collinvht.zenticracing.commands.racing.object.RaceObject;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.StorageVehicle;
import org.bukkit.Location;

public class TeamBaanObject {
    @Getter @Setter
    private RaceObject object;

    @Getter @Setter
    private Location carSpawnLocation;

    @Getter @Setter
    private BaseVehicle baseVehicle;

    @Getter @Setter
    private StorageVehicle vehicle;

    @Getter @Setter
    private StorageVehicle vehicle2;

    @Getter @Setter
    private TeamObject team;

    public TeamBaanObject(RaceObject object, Location carSpawnLocation, TeamObject team) {
        this.object = object;
        this.carSpawnLocation = carSpawnLocation;
        this.team = team;
    }


    public void stopRace() {
        object.stopRace(false);

        if(vehicle != null) {
            if (vehicle.getSpawnedVehicle() != null) {
                vehicle.getSpawnedVehicle().despawn(true);
            }
            VehiclesPlusAPI.getInstance().removeVehicle(vehicle);
        }

        if(vehicle2 != null) {
            if (vehicle2.getSpawnedVehicle() != null) {
                vehicle2.getSpawnedVehicle().despawn(true);
            }
            VehiclesPlusAPI.getInstance().removeVehicle(vehicle2);
        }
    }
}
