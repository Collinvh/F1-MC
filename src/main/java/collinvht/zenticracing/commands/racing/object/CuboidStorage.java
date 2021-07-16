package collinvht.zenticracing.commands.racing.object;

import collinvht.zenticracing.util.objs.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

public class CuboidStorage {

    @Getter @Setter
    private Cuboid s1 = new Cuboid(new Location(Bukkit.getWorlds().get(0), 0,0,0));
    @Getter @Setter
    private Cuboid s2 = new Cuboid(new Location(Bukkit.getWorlds().get(0), 0,0,0));
    @Getter @Setter
    private Cuboid s3 = new Cuboid(new Location(Bukkit.getWorlds().get(0), 0,0,0));
    @Getter @Setter
    private Cuboid pitexit = new Cuboid(new Location(Bukkit.getWorlds().get(0), 0,0,0));
    @Getter @Setter
    private Cuboid pit = new Cuboid(new Location(Bukkit.getWorlds().get(0), 0,0,0));


    @Getter @Setter
    private HashMap<String, Cuboid> detecties = new HashMap<>();

    @Getter @Setter
    private HashMap<String, DRSZone> drsZone = new HashMap<>();


    public void addDetectie(String name, Cuboid cuboid) {
        detecties.put(name, cuboid);
    }

    public void removeDetectie(String name) {
        detecties.remove(name);
    }

    public boolean hasNull() {

        return s1 == null || s2 == null || s3 == null || pitexit == null || pit == null;
    }
}
