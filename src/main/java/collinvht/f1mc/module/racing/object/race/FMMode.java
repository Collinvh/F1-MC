package collinvht.f1mc.module.racing.object.race;

import lombok.Getter;

@Getter
public enum FMMode {
    LOW(0, 1, 0),
    MEDIUM(1, 1.1, 4),
    HIGH(2, 1.3, 8),
    HOTLAP(3, 2, 12);

    private final int id;
    private final double usage;
    private final double extraSpeed;
    FMMode(int id, double usage, double extraSpeed) {
        this.extraSpeed = extraSpeed;
        this.usage = usage;
        this.id = id;
    }
}
