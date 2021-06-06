package collinvht.zenticracing.commands.racing.object;

import lombok.Getter;

public enum RaceMode {
    TRAINING(false, "training", 0, 5),
    RACE(true, "race", 1, 3),
    TRAINING_TEAM(false, "team_training", 420, -1);



    @Getter
    private final boolean hasLaps;
    @Getter
    private final String name;
    @Getter
    private final int id;
    @Getter
    private final int warningMargin;

    RaceMode(boolean laps, String name, int id, int warningMargin) {
        this.hasLaps = laps;
        this.name = name;
        this.id = id;
        this.warningMargin = warningMargin;
    }


    public static RaceMode getModeFromString(String mode) {
        for (RaceMode value : RaceMode.values()) {
            try {
                int id = Integer.parseInt(mode);
                if(value.id == id) {
                    return value;
                }
            } catch (NumberFormatException ignored) {}

            if(value.name.equalsIgnoreCase(mode)) {
                return value;
            }
        }
        return TRAINING;
    }
}
