package collinvht.f1mc.module.vehiclesplus.objects;

import collinvht.f1mc.module.racing.object.laptime.DriverLaptimeStorage;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceListener;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class RaceDriver {

    @Setter @Getter
    private SpawnedVehicle vehicle;
    @Getter
    private boolean isDriving = true;

    private final RaceDriver instace;
    @Getter
    private final HashMap<Race, DriverLaptimeStorage> laptimes = new HashMap<>();

    @Getter
    private boolean isPassedPitExit;

    @Getter
    private boolean isInPit = true;

    @Getter @Setter
    private boolean finished;

    @Getter @Setter
    private long finishTime;

    @Getter @Setter
    private int currentLap;

    @Getter
    private final UUID driverUUID;
    @Getter
    private final Player player;
    @Getter
    private final String driverName;

    @Getter @Setter
    private boolean disqualified;

    @Getter @Setter
    private int speedingFlags;

    @Getter @Setter
    private Sidebar sidebar;

    public RaceDriver(Player player) {
        Bukkit.getLogger().warning(String.valueOf(player.getUniqueId()));
        this.driverUUID = player.getUniqueId();
        this.player = player;
        this.driverName = player.getName();
        initialize();
        this.instace = this;
    }

    private static TimerTask task;
    private static Timer timer;

    private void initialize() {
        task = new TimerTask() {
            @Override
            public void run() {
                if(isDriving) {
                    for (Race race : RaceListener.getLISTENING()) {
                        race.getRaceLapStorage().update(instace);
                    }
                    try {
                        Bukkit.getPlayer(getDriverUUID()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Speed: " + vehicle.getCurrentSpeedInKm() + " | Fuel: " + vehicle.getStorageVehicle().getVehicleStats().getCurrentFuel()));
                    } catch (Exception ignored) {}
                }
            }
        };
        if(timer == null) timer = new Timer("F1MC RaceListener Player | " + driverName);
        timer.schedule(task, 0, 1);
    }

    public DriverLaptimeStorage getLaptimes(Race race) {
        if(!laptimes.containsKey(race)) {
            laptimes.put(race, new DriverLaptimeStorage(race));
        }
        return laptimes.get(race);
    }

    public void setDriving(boolean driving) {
        isDriving = driving;
        isPassedPitExit = false;
    }

    public void setInPit() {
        isInPit = true;
        isPassedPitExit = false;
    }

    public void setPassedPitExit(Race race) {
        isInPit = false;
        isPassedPitExit = true;
        if(laptimes.get(race) != null) {
            if (laptimes.get(race).getCurrentLap() != null) {
                laptimes.get(race).getCurrentLap().getS1().setSectorStart(System.currentTimeMillis() - 1000);
            }
        }
    }

    public void reset() {
        currentLap = 0;
    }

    public void delete() {
        task.cancel();
    }
}
