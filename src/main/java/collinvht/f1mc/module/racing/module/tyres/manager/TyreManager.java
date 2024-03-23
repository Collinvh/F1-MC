package collinvht.f1mc.module.racing.module.tyres.manager;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownObject;
import collinvht.f1mc.module.racing.module.tyres.obj.TyreObject;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class TyreManager extends ModuleBase {
    private static final HashMap<String, TyreObject> tyres = new HashMap<>();
    @Override
    public void load() {
        File files = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/tyres.json").toFile();
        if(files.exists()) {
            try {
                JsonObject object = (JsonObject) Utils.readJson(files.getAbsolutePath());
                JsonArray array = object.getAsJsonArray("tyres");
                for (JsonElement jsonElement : array) {
                    JsonObject object2 = jsonElement.getAsJsonObject();
                    String name = object2.get("name").getAsString();
                    tyres.put(name, new TyreObject(name, object2.get("maxDura").getAsDouble(), object2.get("degradingRate").getAsDouble(), object2.get("steering").getAsDouble(), object2.get("extraSpeed").getAsDouble()));

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            tyres.put("soft", new TyreObject("soft", 1500, 1.15, 1.1, 15));
            tyres.put("medium", new TyreObject("medium", 1650, 1.10, 1.05, 7.5));
            tyres.put("hard", new TyreObject("soft", 1800, 1.05, 1, 0));
        }
    }

    @Override
    public void saveModule() {
        File path = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/").toFile();
        JsonObject object = new JsonObject();
        JsonArray mainObject = new JsonArray();
        tyres.forEach((name, tyre) -> {
            JsonObject object2 = new JsonObject();
            object2.addProperty("name", name);
            object2.addProperty("extraSpeed", tyre.getExtraSpeed());
            object2.addProperty("degradingRate", tyre.getDegradingRate());
            object2.addProperty("steering", tyre.getSteering());
            object2.addProperty("maxDura", tyre.getMaxDurability());
            mainObject.add(object2);
        });
        object.add("tyres", mainObject);

        Utils.saveJSON(path, "tyres", object);
    }
}
