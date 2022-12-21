package collinvht.projectr.util.objects.race;

import lombok.Getter;

public enum RaceMode {
    PRATICE(0, false),
    RACE(1, true);

    @Getter
    private final int id;
    @Getter
    private final boolean hasLaps;
    RaceMode(int id, boolean hasLaps) {
        this.id = id;
        this.hasLaps = hasLaps;
    }

    public static RaceMode getRace(int id) {
        for (RaceMode value : RaceMode.values()) {
            if(value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
