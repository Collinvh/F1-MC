package collinvht.f1mc.module.racing.object.race;

import lombok.Getter;

@Getter
public enum RaceMode {
    PRACTICE(0, false),
    LAPPING(1, true),
    NO_TIMING(2, false);

    private final int modeId;
    private final boolean lapped;
    RaceMode(int modeID, boolean lapped) {
        this.modeId = modeID;
        this.lapped = lapped;
    }

    public static RaceMode getRace(int id) {
        for (RaceMode value : RaceMode.values()) {
            if(value.getModeId() == id) {
                return value;
            }
        }
        return null;
    }
}
