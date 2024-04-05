package collinvht.f1mc.module.racing.module.slowdown.obj;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
public class SlowdownObject {
    private final Material mat;
    @Setter
    private double maxSpeed;
    @Setter
    private double slowdownSpeed;
    @Setter
    private double steeringPercent;

    public SlowdownObject(Material mat, double maxSpeedPercent, double slowDownSpeed, double steeringPercent) {
        this.mat = mat;
        this.maxSpeed = maxSpeedPercent;
        this.slowdownSpeed = slowDownSpeed;
        this.steeringPercent = steeringPercent;
    }
}
