package collinvht.f1mc.module.racing.module.slowdown.obj;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SlowdownIAObject extends SlowdownBase {
    private final String id;

    public SlowdownIAObject(String id, double maxSpeedPercent, double slowDownSpeed, double steeringPercent) {
        super(maxSpeedPercent, slowDownSpeed, steeringPercent);
        this.id = id;
    }
}
