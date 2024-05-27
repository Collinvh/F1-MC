package collinvht.f1mc.module.racing.object.race;

import lombok.Getter;

@Getter
public enum ERSMode {
    OFF(0, 2.1, 0, 0),
    BALANCED(1, 0, 1.6, 9),
    HOTLAP(2, 0, 3, 13.5),
    OVERTAKE(3, 0, 6, 15);

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
