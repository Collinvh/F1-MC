package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.racing.object.Cuboid;
import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RaceCuboidStorage {
    /*
    Sectors
     */
    @Setter @Getter
    private NamedCuboid S1;
    @Setter @Getter
    private NamedCuboid S2;
    @Setter @Getter
    private NamedCuboid S3;

    /*
    Pit
     */
    @Setter @Getter
    private NamedCuboid pitEntry;
    @Setter @Getter
    private NamedCuboid pitExit;

    /*
    Track limits
     */
    @Getter
    private HashMap<String, NamedCuboid> limits = new HashMap<>();

    /*
    Time trial
     */
    @Setter
    @Getter
    private Location timeTrialSpawn;

    @Setter
    @Getter
    private Location timeTrialLeaderboard;


    public static RaceCuboidStorage fromJson(JsonObject cuboids) {
        try {
            RaceCuboidStorage storage = new RaceCuboidStorage();
            for (Map.Entry<String, JsonElement> entries : cuboids.entrySet()) {
                JsonObject object = entries.getValue().getAsJsonObject();
                String cuboidName = entries.getKey();
                if(!cuboidName.equals("offTracks")) {
                    Map<String, String> serializableMap = new HashMap<>();
                    for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.entrySet()) {
                        serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
                    }

                    Cuboid cuboid = Cuboid.deserialize(serializableMap);
                    switch (cuboidName) {
                        case "s1":
                            storage.setS1(new NamedCuboid(cuboid, cuboidName));
                            break;
                        case "s2":
                            storage.setS2(new NamedCuboid(cuboid, cuboidName));
                            break;
                        case "s3":
                            storage.setS3(new NamedCuboid(cuboid, cuboidName));
                            break;
                        case "pitentry":
                            storage.setPitEntry(new NamedCuboid(cuboid, cuboidName));
                            break;
                        case "pitexit":
                            storage.setPitExit(new NamedCuboid(cuboid, cuboidName));
                            break;
                    }
                }
            }
            JsonObject offTracks = cuboids.get("offTracks").getAsJsonObject();
            if (offTracks != null) {
                for (Map.Entry<String, JsonElement> entries : offTracks.entrySet()) {
                    JsonObject object = entries.getValue().getAsJsonObject();
                    String cuboidName = entries.getKey();

                    Map<String, String> serializableMap = new HashMap<>();
                    for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.entrySet()) {
                        serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
                    }
                    Cuboid cuboid = Cuboid.deserialize(serializableMap);
                    storage.getLimits().put(cuboidName, new NamedCuboid(cuboid, cuboidName));
                }
            }
            return storage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public JsonObject toJson() {
        return cuboidsToJson(S1, S2, S3, pitEntry, pitExit);
    }

    public boolean allCuboidsSet() {
        return S1 != null && S2 != null && S3 != null && pitEntry != null && pitExit != null;
    }

    private JsonObject cuboidsToJson(NamedCuboid... cuboids) {
        JsonObject object = new JsonObject();
        for (NamedCuboid namedCuboid : cuboids) {
            if(namedCuboid != null) {
                JsonObject cuboidObj = new JsonObject();
                Cuboid cuboid = namedCuboid.getCuboid();
                cuboid.serialize().forEach((s, o) -> cuboidObj.addProperty(s, String.valueOf(o)));
                object.add(namedCuboid.getName(), cuboidObj);
            }
        }
        JsonObject newObject = new JsonObject();
        if(limits.size() > 0) {
        for (NamedCuboid namedCuboid : limits.values()) {
            if(namedCuboid != null) {
                JsonObject cuboidObj = new JsonObject();
                Cuboid cuboid = namedCuboid.getCuboid();
                cuboid.serialize().forEach((s, o) -> cuboidObj.addProperty(s, String.valueOf(o)));
                newObject.add(namedCuboid.getName(), cuboidObj);
            }
        }
        }
        object.add("offTracks", newObject);
        return object;
    }

    public NamedCuboid createNamedCuboidFromSelection(World world, Region region, String name) {
        Cuboid cuboid = new Cuboid(Utils.blockVectorToLocation(world, region.getMinimumPoint()), Utils.blockVectorToLocation(world, region.getMaximumPoint()));
        Bukkit.getLogger().warning(name);
        return new NamedCuboid(cuboid, name);
    }

    public JsonObject ttSpawnJson() {
        JsonObject object = new JsonObject();
        if(timeTrialSpawn != null) {
            timeTrialSpawn.serialize().forEach(((s, o) -> object.addProperty(s, String.valueOf(o))));
        }
        return object;
    }
    public JsonObject ttLeaderboardJson() {
        JsonObject object = new JsonObject();
        if(timeTrialLeaderboard != null) {
            timeTrialLeaderboard.serialize().forEach(((s, o) -> object.addProperty(s, String.valueOf(o))));
        }
        return object;
    }

    public void setTimeTrialSpawnObj(JsonObject ttSpawn) {
        if(ttSpawn != null) {
            Map<String, Object> serializableMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : ttSpawn.entrySet()) {
                serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
            }
            this.timeTrialSpawn = Location.deserialize(serializableMap);
        }
    }

    public void setTimeTrialLeaderboardObj(JsonObject ttLeaderboard) {
        if(ttLeaderboard != null) {
            Map<String, Object> serializableMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : ttLeaderboard.entrySet()) {
                serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
            }
            this.timeTrialLeaderboard = Location.deserialize(serializableMap);
        }
    }
}
