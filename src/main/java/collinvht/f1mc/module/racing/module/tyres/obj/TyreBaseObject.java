package collinvht.f1mc.module.racing.module.tyres.obj;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TyreBaseObject {
    @Setter
    private double maxDurability;
    @Setter
    private double degradingRate;
    @Setter
    private int modelData;
    @Setter
    private double steering;
    @Setter
    private double extraSpeed;
    private final String name;

    public TyreBaseObject(String name, int modelData, double maxDurability, double degradingRate, double steering, double extraSpeed) {
        this.name = name;
        this.modelData = modelData;
        this.maxDurability = maxDurability;
        this.degradingRate = degradingRate;
        this.steering = steering;
        this.extraSpeed = extraSpeed;
    }
}
