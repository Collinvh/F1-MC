package collinvht.zenticracing.listener.driver.object;

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

    @Getter
    private RaceStorage raceStorage;

    @Getter @Setter
    private Player player;

    @Getter @Setter
    private SpawnedVehicle vehicle;

    public DriverObject(UUID uuid) {
        this.playerUUID = uuid;
        this.lapstorage = new LaptimeStorage();
        this.raceStorage = new RaceStorage();
    }

    public void resetStorage() {
        this.lapstorage = new LaptimeStorage();
        this.raceStorage = new RaceStorage();
    }

}
