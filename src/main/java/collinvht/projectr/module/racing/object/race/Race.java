package collinvht.projectr.module.racing.object.race;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.Utils;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Paths;

public class Race {
    @Getter @Setter
    private int laps;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private RaceCuboidStorage storage;

    @Getter
    private RaceLapStorage raceLapStorage;

    @Getter @Setter
    private RaceFlags flags;

    @Getter @Setter
    private boolean timeTrialStatus;

    public Race(String name, int laps) {
        this.laps = laps;
        this.name = name;
        this.storage = new RaceCuboidStorage();
        this.flags = new RaceFlags();
        this.raceLapStorage = new RaceLapStorage(this);
    }

    public void saveJson() {
        File path = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/races/").toFile();

        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("Name", name);
        mainObject.addProperty("Laps", laps);
        mainObject.addProperty("TimeTrial_Status", timeTrialStatus);
        mainObject.add("TimeTrial_Spawn", storage.ttSpawnJson());
        mainObject.add("Cuboids", storage.toJson());
        mainObject.add("Flags", flags.toJson());

        Utils.saveJSON(path, name, mainObject);
    }

    public static Race createRaceFromJson(JsonObject object) {
        try {
            String name = object.get("Name").getAsString();
            int laps = object.get("Laps").getAsInt();
            boolean ttstatus = object.get("TimeTrial_Status").getAsBoolean();

            RaceCuboidStorage raceStorage = RaceCuboidStorage.fromJson(object.get("Cuboids").getAsJsonObject());
            if(raceStorage != null) {
                raceStorage.setTimeTrialSpawn(object.get("TimeTrial_Spawn").getAsJsonObject());
                Race race = new Race(name, laps);
                race.setFlags(RaceFlags.fromJson(object.get("Flags").getAsJsonObject()));
                race.setTimeTrialStatus(ttstatus);
                race.setStorage(raceStorage);
                return race;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
