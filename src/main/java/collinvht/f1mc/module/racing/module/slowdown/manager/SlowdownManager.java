package collinvht.f1mc.module.racing.module.slowdown.manager;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownBase;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownIAObject;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownObject;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class SlowdownManager extends ModuleBase {
    public static final HashMap<Material, SlowdownBase> slowDowns = new HashMap<>();
    public static final HashMap<String, SlowdownIAObject> customslowDowns = new HashMap<>();

    public static String addBlock(ItemStack stack, double slowdown, double steering, double maxSpeed) {
        Material material = stack.getType();
        if(slowDowns.containsKey(material)) {
            SlowdownBase object = slowDowns.get(material);
            object.setSlowdownSpeed(slowdown);
            object.setSteeringPercent(steering);
            object.setMaxSpeed(maxSpeed);
        } else {
            slowDowns.put(material, new SlowdownObject(material, maxSpeed,slowdown,steering));
        }
        return "Block added.";
    }

    public static String addCustomBlock(String id, double slowdown, double steering, double maxSpeed) {
        if(customslowDowns.containsKey(id)) {
            SlowdownIAObject object = customslowDowns.get(id);
            object.setSlowdownSpeed(slowdown);
            object.setSteeringPercent(steering);
            object.setMaxSpeed(maxSpeed);
        } else {
            customslowDowns.put(id, new SlowdownIAObject(id, maxSpeed,slowdown,steering));
        }
        return "Block added.";
    }

    public static String removeBlock(ItemStack stack) {
        if(slowDowns.containsKey(stack.getType())) {
            slowDowns.remove(stack.getType());
            return "Block geremoved";
        } else {
            return "Block staat niet in de slowdownlist";
        }
    }

    public void saveModule() {
        File path = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/").toFile();
        JsonObject object = new JsonObject();
        JsonArray mainObject = new JsonArray();
        slowDowns.forEach((material, doubles) -> {
            JsonObject object2 = new JsonObject();
            object2.addProperty("Material", material.name());
            object2.addProperty("SlowdownSpeed", doubles.getSlowdownSpeed());
            object2.addProperty("SteeringSpeed", doubles.getSteeringPercent());
            object2.addProperty("MaxSpeed", doubles.getMaxSpeed());
            mainObject.add(object2);
        });
        JsonArray mainObject2 = new JsonArray();
        customslowDowns.forEach((id, doubles) -> {
            JsonObject object2 = new JsonObject();
            object2.addProperty("Identifier", id);
            object2.addProperty("SlowdownSpeed", doubles.getSlowdownSpeed());
            object2.addProperty("SteeringSpeed", doubles.getSteeringPercent());
            object2.addProperty("MaxSpeed", doubles.getMaxSpeed());
            mainObject2.add(object2);
        });
        object.add("array", mainObject);
        object.add("array2", mainObject2);

        Utils.saveJSON(path, "slowdown", object);
    }

    public void load() {
        File files = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/slowdown.json").toFile();
        if(files.exists()) {
            try {
                JsonObject object = (JsonObject) Utils.readJson(files.getAbsolutePath());
                JsonArray array = object.getAsJsonArray("array");
                for (JsonElement jsonElement : array) {
                    JsonObject object2 = jsonElement.getAsJsonObject();
                    Material material = Material.getMaterial(object2.get("Material").getAsString());
                    SlowdownBase slowdownObject = new SlowdownObject(material, object2.get("MaxSpeed").getAsDouble(),object2.get("SlowdownSpeed").getAsDouble(),object2.get("SteeringSpeed").getAsDouble());
                    slowDowns.put(material, slowdownObject);
                }
                JsonArray array2 = object.getAsJsonArray("array2");
                if(array2 != null) {
                    for (JsonElement jsonElement : array2) {
                        JsonObject object2 = jsonElement.getAsJsonObject();
                        String id = object2.get("Identifier").getAsString();
                        SlowdownIAObject slowdownObject = new SlowdownIAObject(id, object2.get("MaxSpeed").getAsDouble(), object2.get("SlowdownSpeed").getAsDouble(), object2.get("SteeringSpeed").getAsDouble());
                        customslowDowns.put(id, slowdownObject);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
