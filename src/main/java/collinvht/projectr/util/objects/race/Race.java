package collinvht.projectr.util.objects.race;

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
    private RaceStorage storage;

    @Getter @Setter
    private boolean timeTrialStatus;

    public Race(String name, int laps) {
        this.laps = laps;
        this.name = name;
        this.storage = new RaceStorage();
    }

    public void saveJson() {
        File path = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/races/").toFile();

        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("Name", name);
        mainObject.addProperty("Laps", laps);
        mainObject.add("TT_Spawn", storage.ttSpawnJson());
        mainObject.add("Cuboids", storage.toJson());

        Utils.saveJSON(path, name, mainObject);
    }

    public static Race createRaceFromJson(JsonObject object) {
        try {
            String name = object.get("Name").getAsString();
            int laps = object.get("Laps").getAsInt();

            RaceStorage raceStorage = RaceStorage.fromJson(object.get("Cuboids").getAsJsonObject());
            if(raceStorage != null) {
                raceStorage.setTimeTrialSpawn(object.get("TT_Spawn").getAsJsonObject());
                Race race = new Race(name, laps);
                race.setStorage(raceStorage);
                return race;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
