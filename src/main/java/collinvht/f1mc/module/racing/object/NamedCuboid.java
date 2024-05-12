package collinvht.f1mc.module.racing.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class NamedCuboid {
    @Getter @Setter
    private Cuboid cuboid;
    @Getter @Setter
    private String name;

    public NamedCuboid(Cuboid cuboid, String name) {
        this.cuboid = cuboid;
        this.name = name;
    }

    public boolean contains(Location location) {
        return cuboid.containsLocation(location);
    }

    public boolean contains(Vector location) {
        return cuboid.containsVector(location);
    }
}