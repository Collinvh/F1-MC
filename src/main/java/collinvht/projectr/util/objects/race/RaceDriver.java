package collinvht.projectr.util.objects.race;

import collinvht.projectr.util.objects.race.laptime.LaptimeStorage;
import collinvht.projectr.util.objects.race.laptime.Laptimes;
import lombok.Getter;
import lombok.Setter;
import nl.mtvehicles.core.infrastructure.models.Vehicle;

import java.util.LinkedList;
import java.util.UUID;

public class RaceDriver {
    @Setter @Getter
    private Vehicle vehicle;
    @Getter
    private boolean isDriving;

    @Getter @Setter
    private Laptimes laptimes;

    @Getter
    private boolean isPassedPitExit;

    @Getter
    private boolean isInPit = true;

    @Getter @Setter
    private boolean finished;

    @Getter @Setter
    private int currentLap;

    @Getter
    private final UUID driverUUID;

    public RaceDriver(UUID uuid) {
        this.laptimes = new Laptimes();
        this.driverUUID = uuid;
    }

    public void addLaptime(LaptimeStorage storage) {
        laptimes.addLaptime(storage);
    }

    public void setDriving(boolean driving) {
        isDriving = driving;
        isInPit = true;
        isPassedPitExit = false;
    }

    public void setInPit() {
        isInPit = true;
        isPassedPitExit = false;
    }

    public void setPassedPitExit() {
        isInPit = false;
        isPassedPitExit = true;
        if(laptimes.getCurrentLap() != null) {
            laptimes.getCurrentLap().getS1data().setSectorStart(System.currentTimeMillis());
        }
    }
}
