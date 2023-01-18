package collinvht.projectr.util.objects.vehicle;

import lombok.Getter;
import lombok.Setter;

public class Tyre {
    @Getter @Setter
    private double maxDurability;

    @Getter @Setter
    private int modelData;

    @Getter @Setter
    private double durability;

    @Getter @Setter
    private int extraSpeed;

    @Getter @Setter
    private int degradingRate;

    @Getter @Setter
    private String prefix;
}
