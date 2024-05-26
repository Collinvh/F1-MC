package collinvht.f1mc.module.racing.object.race;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



@Getter
public class RaceFlags {
    @Setter
    private ArrayList<Location> s1loc = new ArrayList<>();
    @Setter
    private ArrayList<Location> s2loc = new ArrayList<>();
    @Setter
    private ArrayList<Location> s3loc = new ArrayList<>();

    private FlagType S1;
    private FlagType S2;
    private FlagType S3;


    public void setS1(FlagType s1) {
        S1 = s1;
        World world = null;
        for (Location location : this.s1loc) {
            if(world == null) world = location.getWorld();
            if(world != null) {
                world.setBlockData(location, s1.getColorMaterial().createBlockData());
            }
        }
    }

    public void setS2(FlagType s2) {
        S2 = s2;
        World world = null;
        for (Location location : this.s2loc) {
            if(world == null) world = location.getWorld();
            if(world != null) {
                world.setBlockData(location, s2.getColorMaterial().createBlockData());
            }
        }
    }

    public void setS3(FlagType s3) {
        S3 = s3;
        World world = null;
        for (Location location : this.s3loc) {
            if(world == null) world = location.getWorld();
            if(world != null) {
                world.setBlockData(location, s3.getColorMaterial().createBlockData());
            }
        }
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        JsonArray array1 = new JsonArray();
        for (Location location : s1loc) {
            JsonObject jsonObject = new JsonObject();
            location.serialize().forEach(((s, o) -> jsonObject.addProperty(s, String.valueOf(o))));
            array1.add(jsonObject);
        }
        object.add("s1loc", array1);
        JsonArray array2 = new JsonArray();
        for (Location location : s2loc) {
            JsonObject jsonObject = new JsonObject();
            location.serialize().forEach(((s, o) -> jsonObject.addProperty(s, String.valueOf(o))));
            array2.add(jsonObject);
        }
        object.add("s2loc", array2);
        JsonArray array3 = new JsonArray();
        for (Location location : s3loc) {
            JsonObject jsonObject = new JsonObject();
            location.serialize().forEach(((s, o) -> jsonObject.addProperty(s, String.valueOf(o))));
            array3.add(jsonObject);
        }
        object.add("s3loc", array3);

        return object;
    }

    public static RaceFlags fromJson(JsonObject object) {
        JsonArray array1 = object.getAsJsonArray("s1loc");
        JsonArray array2 = object.getAsJsonArray("s2loc");
        JsonArray array3 = object.getAsJsonArray("s3loc");

        ArrayList<Location> s1loc = new ArrayList<>();
        for (JsonElement jsonElement : array1) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            Map<String, Object> serializableMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObject.entrySet()) {
                serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
            }
            Location location = Location.deserialize(serializableMap);
            s1loc.add(location);
        }
        ArrayList<Location> s2loc = new ArrayList<>();
        for (JsonElement jsonElement : array2) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            Map<String, Object> serializableMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObject.entrySet()) {
                serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
            }
            Location location = Location.deserialize(serializableMap);
            s2loc.add(location);
        }
        ArrayList<Location> s3loc = new ArrayList<>();
        for (JsonElement jsonElement : array3) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            Map<String, Object> serializableMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObject.entrySet()) {
                serializableMap.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
            }
            Location location = Location.deserialize(serializableMap);
            s3loc.add(location);
        }

        RaceFlags flags = new RaceFlags();
        flags.setS1loc(s1loc);
        flags.setS2loc(s2loc);
        flags.setS3loc(s3loc);
        return flags;
    }
}
