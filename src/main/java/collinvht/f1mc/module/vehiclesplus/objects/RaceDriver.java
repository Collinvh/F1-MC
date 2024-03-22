package collinvht.f1mc.module.vehiclesplus.objects;

import collinvht.f1mc.module.racing.object.laptime.DriverLaptimeStorage;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.module.racing.object.race.Race;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class RaceDriver {
    @Setter @Getter
    private SpawnedVehicle vehicle;
    @Getter
    private boolean isDriving = true;

    @Getter
    private final HashMap<Race, DriverLaptimeStorage> laptimes = new HashMap<>();

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
    @Getter
    private final String driverName;

    @Getter @Setter
    private boolean disqualified;

    public RaceDriver(Player player) {
        this.driverUUID = player.getUniqueId();
        this.driverName = player.getName();
//        SetupManager.initializeSetup(uuid);
    }

    public DriverLaptimeStorage getLaptimes(Race race) {
        if(!laptimes.containsKey(race)) {
            laptimes.put(race, new DriverLaptimeStorage(race));
        }
        return laptimes.get(race);
    }

    public void addLaptime(Race race, LaptimeStorage storage) {
        laptimes.get(race).addLaptime(storage);
//        DiscordManager.updateTimings(RaceMode.PRATICE);
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

    public void setPassedPitExit(Race race) {
        isInPit = false;
        isPassedPitExit = true;
        if(laptimes.get(race).getCurrentLap() != null) {
            laptimes.get(race).getCurrentLap().getS1data().setSectorStart(System.currentTimeMillis());
        }
    }
}
