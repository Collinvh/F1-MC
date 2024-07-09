package collinvht.f1mc.module.racing.module.slowdown.obj;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
public class SlowdownObject extends SlowdownBase {
    private final Material mat;

    public SlowdownObject(Material mat, double maxSpeedPercent, double slowDownSpeed, double steeringPercent) {
        super(maxSpeedPercent, slowDownSpeed, steeringPercent);
        this.mat = mat;
    }
}
