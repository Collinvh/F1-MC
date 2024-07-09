package collinvht.f1mc.module.racing.module.slowdown.obj;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class SlowdownBase {
    protected double maxSpeed;
    protected double slowdownSpeed;
    protected double steeringPercent;

    public SlowdownBase(double maxSpeedPercent, double slowDownSpeed, double steeringPercent) {
        this.maxSpeed = maxSpeedPercent;
        this.slowdownSpeed = slowDownSpeed;
        this.steeringPercent = steeringPercent;
    }
}
