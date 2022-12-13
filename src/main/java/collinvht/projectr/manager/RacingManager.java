package collinvht.projectr.manager;
import lombok.Getter;

public class RacingManager {
    @Getter
    private static RacingManager instance;

    private RacingManager() {
        loadRaces();
    }

    public static void initialize() {
        instance = new RacingManager();
    }

    public static void disable() {
        instance.saveRaces();
    }
    private void saveRaces() {

    }

    private void loadRaces() {

    }
    public String startRace(String raceName, String mode) {
        return null;
    }
    public String stopRace(String raceName) {
        return null;
    }
    public String deleteRace(String raceName) {
        return null;
    }

    public String getRaceResult() {
        return null;
    }

    public String createRace(String raceName, String laps) {
        return null;
    }

    public String listRaces() {
        return null;
    }
}
