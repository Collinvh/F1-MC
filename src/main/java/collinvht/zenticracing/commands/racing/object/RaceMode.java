package collinvht.zenticracing.commands.racing.object;

import lombok.Getter;

public enum RaceMode {
    TRAINING(false, "training", 0),
    RACING(true, "racing", 1),
    TRAINING_TEAM(false, "team_training", 420);



    @Getter
    private final boolean hasLaps;
    @Getter
    private final String name;
    @Getter
    private final int id;

    RaceMode(boolean laps, String name, int id) {
        this.hasLaps = laps;
        this.name = name;
        this.id = id;
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
