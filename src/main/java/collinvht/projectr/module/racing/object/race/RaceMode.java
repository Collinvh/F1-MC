package collinvht.projectr.module.racing.object.race;

import lombok.Getter;

public enum RaceMode {
    PRACTICE(0, false),
    LAPPING(1, true);

    @Getter
    private final int modeId;
    @Getter
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
