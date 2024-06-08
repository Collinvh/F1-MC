package collinvht.f1mc.module.vehiclesplus.objects;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.object.laptime.DriverLaptimeStorage;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceCar;
import collinvht.f1mc.module.racing.object.race.RaceListener;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import collinvht.f1mc.util.Utils;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import scala.Int;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RaceDriver {

    @Setter @Getter
    private SpawnedVehicle vehicle;
    @Getter @Setter
    private RaceCar raceCar;
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

    private ScheduledTask task;

    private void initialize() {
        task = F1MC.getAsyncScheduler().runAtFixedRate(F1MC.getInstance(), new Consumer<ScheduledTask>() {
            @Override
            public void accept(ScheduledTask scheduledTask) {
                if(isDriving) {
                    for (Race race : RaceListener.getLISTENING()) {
                        race.getRaceLapStorage().update(instace);
                    }
                    try {
                        if(raceCar != null) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GRAY + "| " + vehicle.getCurrentSpeedInKm() + "km/h | " + (int) (raceCar.getCurrentERS()/200*100) + "% | " + (int)(double)vehicle.getStorageVehicle().getVehicleStats().getCurrentFuel() + "/" + vehicle.getStorageVehicle().getVehicleStats().getFuelTank() + "L"));
                        } else if(!Utils.isEnableTimeTrial()) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GRAY + "| " + vehicle.getCurrentSpeedInKm() + "km/h | " + (int)(double)vehicle.getStorageVehicle().getVehicleStats().getCurrentFuel() + "/" + vehicle.getStorageVehicle().getVehicleStats().getFuelTank() + "L"));
                        } else {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GRAY + "| " + vehicle.getCurrentSpeedInKm() + "km/h | "));
                        }
                    } catch (Exception ignored) {}
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
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
