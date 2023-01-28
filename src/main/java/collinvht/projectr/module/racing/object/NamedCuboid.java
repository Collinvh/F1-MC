package collinvht.projectr.module.racing.object;

import lombok.Getter;
import lombok.Setter;

public class NamedCuboid {
    @Getter @Setter
    private Cuboid cuboid;
    @Getter @Setter
    private String name;

    public NamedCuboid(Cuboid cuboid, String name) {
        this.cuboid = cuboid;
        this.name = name;
    }
}