package collinvht.projectr.util.objects.vehicle;

import collinvht.projectr.util.objects.race.RaceDriver;
import lombok.Getter;
import lombok.Setter;
import nl.mtvehicles.core.infrastructure.models.Vehicle;

public class RaceCar {

    @Getter @Setter
    private Team team;

    @Getter @Setter
    private RaceDriver driver;
    @Getter @Setter
    private Tyre currentTyre;

    @Getter @Setter
    private Vehicle vehicle;

    @Getter @Setter
    private ErsBattery battery;
    public RaceCar(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.battery = new ErsBattery();
    }

    public boolean canDrive() {
        if(currentTyre == null) return false;
        if(currentTyre.getDurability() == 0) return false;
        return driver.isDriving();
    }
}
