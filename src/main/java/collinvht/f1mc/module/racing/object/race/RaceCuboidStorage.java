package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.racing.object.Cuboid;
import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.module.racing.object.PenaltyCuboid;
import collinvht.f1mc.util.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RaceCuboidStorage {
    /*
    Sectors
     */
    @Setter @Getter
    private HashMap<String, NamedCuboid> S1_mini = new HashMap<>();
    @Setter @Getter
    private NamedCuboid S1;
    @Setter @Getter
    private HashMap<String, NamedCuboid> S2_mini = new HashMap<>();
    @Setter @Getter
    private NamedCuboid S2;
    @Setter @Getter
    private HashMap<String, NamedCuboid> S3_mini = new HashMap<>();
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
    private HashMap<String, PenaltyCuboid> limits = new HashMap<>();

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
                if(!cuboidName.equals("offTracks") && !cuboidName.contains("_mini")) {
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
                    for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.getAsJsonObject("cuboidHolder").entrySet()) {
                        serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
                    }
                    Cuboid cuboid = Cuboid.deserialize(serializableMap);
                    int flags = object.get("extraFlags").getAsInt();
                    storage.getLimits().put(cuboidName, new PenaltyCuboid(cuboid, cuboidName, 0));
                }
            }

            JsonElement s1_mini = cuboids.get("s1_mini");
            if(s1_mini != null) {
                if(s1_mini instanceof JsonObject s1_mini_obj) {
                    for (Map.Entry<String, JsonElement> entries : s1_mini_obj.entrySet()) {
                        JsonObject object = entries.getValue().getAsJsonObject();
                        String cuboidName = entries.getKey();

                        Map<String, String> serializableMap = new HashMap<>();
                        for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.entrySet()) {
                            serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
                        }
                        Cuboid cuboid = Cuboid.deserialize(serializableMap);
                        storage.getS1_mini().put(cuboidName, new NamedCuboid(cuboid, cuboidName));
                    }
                }
            }
            JsonElement s2_mini = cuboids.get("s2_mini");
            if(s2_mini != null) {
                if(s2_mini instanceof JsonObject s2_mini_obj) {
                    for (Map.Entry<String, JsonElement> entries : s2_mini_obj.entrySet()) {
                        JsonObject object = entries.getValue().getAsJsonObject();
                        String cuboidName = entries.getKey();

                        Map<String, String> serializableMap = new HashMap<>();
                        for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.entrySet()) {
                            serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
                        }
                        Cuboid cuboid = Cuboid.deserialize(serializableMap);
                        storage.getS2_mini().put(cuboidName, new NamedCuboid(cuboid, cuboidName));
                    }
                }
            }
            JsonElement s3_mini = cuboids.get("s3_mini");
            if(s3_mini != null) {
                if(s3_mini instanceof JsonObject s3_mini_obj) {
                    for (Map.Entry<String, JsonElement> entries : s3_mini_obj.entrySet()) {
                        JsonObject object = entries.getValue().getAsJsonObject();
                        String cuboidName = entries.getKey();

                        Map<String, String> serializableMap = new HashMap<>();
                        for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.entrySet()) {
                            serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
                        }
                        Cuboid cuboid = Cuboid.deserialize(serializableMap);
                        storage.getS3_mini().put(cuboidName, new NamedCuboid(cuboid, cuboidName));
                    }
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
        JsonObject s1_minis = arrayToObject(S1_mini.values());
        if(s1_minis != null) object.add("s1_mini", s1_minis);
        JsonObject s2_minis = arrayToObject(S2_mini.values());
        if(s2_minis != null) object.add("s2_mini", s2_minis);
        JsonObject s3_minis = arrayToObject(S3_mini.values());
        if(s3_minis != null) object.add("s3_mini", s3_minis);

        JsonObject newObject = new JsonObject();
        if(!limits.isEmpty()) {
            for (PenaltyCuboid namedCuboid : limits.values()) {
                if (namedCuboid != null) {
                    int extraFlags = namedCuboid.getExtraFlags();
                    JsonObject cuboidObj = new JsonObject();
                    JsonObject cuboidHolder = new JsonObject();
                    Cuboid cuboid = namedCuboid.getCuboid();
                    cuboid.serialize().forEach((s, o) -> cuboidHolder.addProperty(s, String.valueOf(o)));
                    cuboidObj.add("cuboidHolder", cuboidHolder);
                    cuboidObj.addProperty("extraFlags", extraFlags);
                    newObject.add(namedCuboid.getName(), cuboidObj);
                }
            }
        }
        object.add("offTracks", newObject);
        return object;
    }

    private JsonObject arrayToObject(Collection<NamedCuboid> arrayList) {
        if(arrayList.isEmpty()) return null;
        JsonObject object = new JsonObject();
        for (NamedCuboid namedCuboid : arrayList) {
            JsonObject cuboidObj = new JsonObject();
            Cuboid cuboid = namedCuboid.getCuboid();
            cuboid.serialize().forEach((s, o) -> cuboidObj.addProperty(s, String.valueOf(o)));
            object.add(namedCuboid.getName(), cuboidObj);
        }
        return object;
    }

    public NamedCuboid createNamedCuboidFromSelection(World world, Region region, String name) {
        Cuboid cuboid = new Cuboid(Utils.blockVectorToLocation(world, region.getMinimumPoint()), Utils.blockVectorToLocation(world, region.getMaximumPoint()));
        Bukkit.getLogger().warning(name);
        return new NamedCuboid(cuboid, name);
    }

    public PenaltyCuboid createPenaltyCuboidFromSelection(World world, Region region, String name, int extraFlags) {
        Cuboid cuboid = new Cuboid(Utils.blockVectorToLocation(world, region.getMinimumPoint()), Utils.blockVectorToLocation(world, region.getMaximumPoint()));
        Bukkit.getLogger().warning(name);
        return new PenaltyCuboid(cuboid, name, extraFlags);
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
