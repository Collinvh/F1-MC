package collinvht.projectr.commands.racing.object;

import collinvht.projectr.util.objs.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DRSZone {
    @Getter @Setter
    private Cuboid detectieCuboid = new Cuboid(new Location(Bukkit.getWorlds().get(0), 0,0,0));
    @Getter @Setter
    private Cuboid drsStraight = new Cuboid(new Location(Bukkit.getWorlds().get(0), 0,0,0));

    @Getter @Setter
    private long lastEntryOnDetectie = -1;

    public DRSZone() {
    }
}
