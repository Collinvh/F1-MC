package collinvht.f1mc.module.racing.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class PenaltyCuboid extends NamedCuboid {
    @Getter @Setter
    private int extraFlags;

    public PenaltyCuboid(Cuboid cuboid, String name, int extraFlags) {
        super(cuboid, name);
        this.extraFlags = extraFlags;
    }
}