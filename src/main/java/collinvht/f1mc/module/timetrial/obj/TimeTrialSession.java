package collinvht.f1mc.module.timetrial.obj;

import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceLapStorage;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@Getter
public class TimeTrialSession {
    private final Player player;
    private final Location prevLoc;
    private final SpawnedVehicle spawnedVehicle;
    private TimerTask task;
    private final Race race;
    private static Timer timer;
    private final RaceLapStorage storage;

    public TimeTrialSession(Player player, Location prevLoc, SpawnedVehicle spawnedVehicle, Race race) {
        this.player = player;
        this.prevLoc = prevLoc;
        this.spawnedVehicle = spawnedVehicle;
        this.race = race;
        this.storage = new RaceLapStorage(race);
        startSession();
    }
    public void startSession() {
        storage.setRaceMode(RaceMode.PRACTICE);
        task = new TimerTask() {
            @Override
            public void run() {
                RaceDriver driver = VPListener.getRACE_DRIVERS().get(player.getUniqueId());
                if(driver != null) {
                    storage.update(driver);
                    if(driver.isInPit()) driver.setPassedPitExit(race);
                } else {
                    RaceDriver driver1 = new RaceDriver(player);
                    driver1.setVehicle(spawnedVehicle);
                    VPListener.getRACE_DRIVERS().put(player.getUniqueId(), driver1);
                }
            }
        };
        if(timer == null) timer = new Timer("F1MC Timetrial");
        timer.scheduleAtFixedRate(task, 0, 1);
    }

    public void stop() {
        VPListener.getRACE_DRIVERS().get(player.getUniqueId()).getLaptimes(race).setInvalidated(false);
        timer.cancel();
    }
}
