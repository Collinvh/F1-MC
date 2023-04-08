package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.racing.object.Cuboid;
import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.util.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

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
    Time trial
     */
    @Getter
    private Location timeTrialSpawn;

    public static RaceCuboidStorage fromJson(JsonObject cuboids) {
        try {
            RaceCuboidStorage storage = new RaceCuboidStorage();
            for (Map.Entry<String, JsonElement> entries : cuboids.entrySet()) {
                JsonObject object = entries.getValue().getAsJsonObject();
                String cuboidName = entries.getKey();

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
            return storage;
        } catch (Exception e) {
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
        return object;
    }

    public NamedCuboid createNamedCuboidFromSelection(World world, Region region, String name) {
        Cuboid cuboid = new Cuboid(Utils.blockVectorToLocation(world, region.getMinimumPoint()), Utils.blockVectorToLocation(world, region.getMaximumPoint()));
        return new NamedCuboid(cuboid, name);
    }

    public JsonObject ttSpawnJson() {
        JsonObject object = new JsonObject();
        if(timeTrialSpawn != null) {
            timeTrialSpawn.serialize().forEach(((s, o) -> object.addProperty(s, String.valueOf(o))));
        }
        return object;
    }


    public void setTimeTrialSpawn(Location timeTrialSpawn) {
        this.timeTrialSpawn = timeTrialSpawn;
    }

    public void setTimeTrialSpawn(JsonObject ttSpawn) {
        if(ttSpawn != null) {
            Map<String, Object> serializableMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : ttSpawn.entrySet()) {
                serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
            }
            this.timeTrialSpawn = Location.deserialize(serializableMap);
        }
    }
}
