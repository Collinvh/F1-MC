package collinvht.projectr.util.objs;

import lombok.Getter;

public class TyreConfigData {
    @Getter private final int dura;
    @Getter private final float degradingrate;
    @Getter private final float steering;
    @Getter private final int extraspeed;
    @Getter private final int wetspeed;

    public TyreConfigData(int dura, float degradingrate, float steering, int extraspeed, int wetspeed) {
        this.dura = dura;
        this.degradingrate = degradingrate;
        this.steering = steering;
        this.extraspeed = extraspeed;
        this.wetspeed = wetspeed;
    }
}
