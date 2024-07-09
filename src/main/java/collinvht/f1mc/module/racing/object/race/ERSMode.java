package collinvht.f1mc.module.racing.object.race;

import lombok.Getter;

@Getter
public enum ERSMode {
    OFF(0, 3.6, 0, 0),
    BALANCED(1, 0, 1.0, 4),
    HOTLAP(2, 0, 4.3, 8),
    OVERTAKE(3, 0, 8, 10);

    private final int id;
    private final double regain;
    private final double usage;
    private final double extraSpeed;
    ERSMode(int id, double regain, double usage, double extraSpeed) {
        this.extraSpeed = extraSpeed;
        this.usage = usage;
        this.regain = regain;
        this.id = id;
    }
}
