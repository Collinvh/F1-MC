package collinvht.projectr.util.objects;

import collinvht.projectr.util.objects.race.Race;
import collinvht.projectr.util.objects.race.RaceListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class TimeTrialSession {
    @Getter
    private final UUID timeTrialPlayer;

    @Getter
    private final String vehiclePlate;

    @Getter
    private final Race race;

    @Getter
    private final Location tpLocation;

    @Getter
    private final RaceListener listener;

    public TimeTrialSession(UUID player, String plate, Race race, Location location) {
        this.timeTrialPlayer = player;
        this.vehiclePlate = plate;
        this.race = race;
        this.tpLocation = location;
        this.listener = new RaceListener();
        Bukkit.getLogger().warning(this.listener.startListeningTo(race, 2, player));
    }

    public void quit() {
        Bukkit.getLogger().warning("Race gestopt.");
        this.listener.stopListening();
    }
}
