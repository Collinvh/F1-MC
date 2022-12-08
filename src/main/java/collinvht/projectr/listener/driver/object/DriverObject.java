package collinvht.projectr.listener.driver.object;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.racing.computer.RaceCar;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DriverObject {
    @Getter
    private final UUID playerUUID;

    @Getter @Setter
    private boolean driving;

    @Getter
    private LaptimeStorage lapstorage;

    @Setter @Getter
    private BukkitRunnable curRunnable;

    @Getter @Setter
    private boolean blackFlagged = false;

    @Getter
    private boolean hasDRS = false;

    @Getter @Setter
    private boolean hadDRS = false;

    @Getter @Setter
    private boolean isInDrsZone = false;

    @Getter
    private RaceStorage raceStorage;

    @Getter @Setter
    private Player player;

    @Getter @Setter
    private RaceCar vehicle;
    @Getter @Setter
    private SpawnedVehicle curvehicle;

    public DriverObject(UUID uuid) {
        this.playerUUID = uuid;
        this.lapstorage = new LaptimeStorage();
        this.raceStorage = new RaceStorage();
    }

    public void resetStorage() {
        this.lapstorage = new LaptimeStorage();
        this.raceStorage = new RaceStorage();
        this.blackFlagged = false;
    }


    public void setHasDRS(boolean hasDRS) {
        this.hasDRS = hasDRS;

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!isInDrsZone) {
                    setHasDRS(false);
                } else {
                    setHadDRS(true);
                }
                getRaceStorage().setPastDRSPoint(false);
            }
        }.runTaskLater(ProjectR.getRacing(), 200);
    }
}
