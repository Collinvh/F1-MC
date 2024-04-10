package collinvht.f1mc.module.timetrial.obj;

import collinvht.f1mc.module.racing.object.laptime.DriverLaptimeStorage;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceLapStorage;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import collinvht.f1mc.module.timetrial.command.TimeTrialManager;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@Getter
public class TimeTrialSession {
    private final Player player;
    private final Location prevLoc;
    private final SpawnedVehicle spawnedVehicle;
    private final Race race;
    private final RaceLapStorage storage;
    private RaceDriver driver;
    private boolean isCanceled;
    @Setter
    private TimerTask task;

    public TimeTrialSession(Player player, Location prevLoc, SpawnedVehicle spawnedVehicle, Race race) {
        this.player = player;
        this.prevLoc = prevLoc;
        this.spawnedVehicle = spawnedVehicle;
        this.race = race;
        this.storage = new RaceLapStorage(race);
        storage.setRaceMode(RaceMode.TIMETRIAL);
        driver = VPListener.getRACE_DRIVERS().get(player.getUniqueId());
        if(driver == null) driver = new RaceDriver(player);
        driver.setVehicle(spawnedVehicle);
        driver.setDriving(true);
    }

    public void update() {
        storage.update(driver);
    }

    public void setCanceled() {
        task.cancel();
        isCanceled = true;
    }
}
