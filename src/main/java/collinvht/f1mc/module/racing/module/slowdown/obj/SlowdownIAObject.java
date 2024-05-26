package collinvht.f1mc.module.racing.module.slowdown.obj;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SlowdownIAObject {
    private final String id;
    @Setter
    private double maxSpeed;
    @Setter
    private double slowdownSpeed;
    @Setter
    private double steeringPercent;

    public SlowdownIAObject(String id, double maxSpeedPercent, double slowDownSpeed, double steeringPercent) {
        this.id = id;
        this.maxSpeed = maxSpeedPercent;
        this.slowdownSpeed = slowDownSpeed;
        this.steeringPercent = steeringPercent;
    }
}
