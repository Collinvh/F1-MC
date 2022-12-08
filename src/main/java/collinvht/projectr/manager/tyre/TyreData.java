package collinvht.projectr.manager.tyre;

import lombok.Getter;

public class TyreData {
    @Getter
    private final Tyres tyre;
    @Getter
    private final double durability;

    public TyreData(Tyres tyre, double durability) {
        this.tyre = tyre;
        this.durability = durability;
    }

}
