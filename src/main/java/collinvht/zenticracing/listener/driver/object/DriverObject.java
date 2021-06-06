package collinvht.zenticracing.listener.driver.object;

import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.commands.racing.object.ERSStorage;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DriverObject {
    @Getter
    private final UUID playerUUID;

    @Getter @Setter
    private boolean driving;

    @Getter
    private LaptimeStorage lapstorage;

    @Getter @Setter
    private boolean blackFlagged = false;

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

}
