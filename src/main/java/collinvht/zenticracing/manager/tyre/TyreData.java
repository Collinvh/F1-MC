package collinvht.zenticracing.manager.tyre;

import lombok.Getter;

public class TyreData {
    @Getter
    private final Tyres tyre;
    @Getter
    private final double dura;

    public TyreData(Tyres tyre, double dura) {
        this.tyre = tyre;
        this.dura = dura;
    }

}
