package collinvht.zenticracing.util;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.manager.tyre.Tyres;
import collinvht.zenticracing.util.objs.TyreConfigData;
import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class ConfigUtil {

    private static JsonObject object;

    private static final HashMap<Integer, TyreConfigData> tyres = new HashMap<>();

    @Setter
    private static boolean bestellenEnabled;

    private static JsonObject createDefaultConfig() {
        File config = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/config" + ".json").toFile();
        if(!config.exists()) {
            JsonObject configMain = new JsonObject();

            /*
            TyreData
             */
            JsonArray array = new JsonArray();
            for (Tyres value : Tyres.values()) {
                JsonObject tyreInformation = new JsonObject();
                tyreInformation.addProperty("Name", value.getName());
                tyreInformation.addProperty("TyreID", value.getTyreID());
                tyreInformation.addProperty("Dura", 0);
                tyreInformation.addProperty("DegradingRate", 0.0F);
                tyreInformation.addProperty("Steering", 0.0F);
                tyreInformation.addProperty("ExtraSpeed", 0);
                tyreInformation.addProperty("WetSpeed", 0);

                array.add(tyreInformation);
            }

            configMain.add("Tyres", array);

            /*
            Team info
             */
            JsonArray array1 = new JsonArray();
            JsonObject object = new JsonObject();
            object.addProperty("BestellenEnabled", true);
            array1.add(object);

            configMain.add("TeamInfo", array1);

            try {
                FileWriter writer = new FileWriter(config);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(configMain));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return configMain;
        } else {
            try {
                return (JsonObject) readJson(config.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void loadConfig() {
        JsonObject object = createDefaultConfig();
        if(object != null) {
            ConfigUtil.object = object;

            JsonArray array = ConfigUtil.object.getAsJsonArray("Tyres");
            for (JsonElement element : array) {
                JsonObject object1 = (JsonObject) element;

                int id = object1.get("TyreID").getAsInt();
                int dura = object1.get("Dura").getAsInt();
                float degradingRate = object1.get("DegradingRate").getAsFloat();
                float steer = object1.get("Steering").getAsInt();
                int extraSpeed = object1.get("ExtraSpeed").getAsInt();
                int wetSpeed = object1.get("WetSpeed").getAsInt();


                Bukkit.getLogger().warning("" + id + " " + dura + " " + steer + " " + degradingRate + " " + extraSpeed + " " + wetSpeed);
                ConfigUtil.tyres.put(id, new TyreConfigData(dura, degradingRate,steer,extraSpeed,wetSpeed));
            }

            bestellenEnabled = canBestell();

            Tyres.NULLTYRE.setData(getDataFromInt(0));
            Tyres.SOFT.setData(getDataFromInt(1));
            Tyres.MEDIUM.setData(getDataFromInt(2));
            Tyres.HARD.setData(getDataFromInt(3));
            Tyres.INTER.setData(getDataFromInt(4));
            Tyres.WET.setData(getDataFromInt(5));
            Tyres.BRIDGESTONE.setData(getDataFromInt(6));

        } else {
            Bukkit.getLogger().warning("DISABLING PLUGIN CONFIG NOT FOUND!");
            Bukkit.getPluginManager().disablePlugin(ZenticRacing.getRacing());
        }
    }

    public static boolean canBestell() {
        return bestellenEnabled;
    }

    public static void save() {
        File config = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/config" + ".json").toFile();
        JsonObject configMain = new JsonObject();

        /*
        TyreData
         */
        JsonArray array = new JsonArray();
        for (Tyres value : Tyres.values()) {
            JsonObject tyreInformation = new JsonObject();
            tyreInformation.addProperty("Name", value.getName());
            tyreInformation.addProperty("TyreID", value.getTyreID());
            tyreInformation.addProperty("Dura", value.getData().getDura());
            tyreInformation.addProperty("DegradingRate", value.getData().getDegradingrate());
            tyreInformation.addProperty("Steering", value.getData().getSteering());
            tyreInformation.addProperty("ExtraSpeed", value.getData().getExtraspeed());
            tyreInformation.addProperty("WetSpeed", value.getData().getWetspeed());

            array.add(tyreInformation);
        }

        configMain.add("Tyres", array);

        /*
        Team info
         */
        JsonArray array1 = new JsonArray();
        JsonObject object = new JsonObject();
        object.addProperty("BestellenEnabled", bestellenEnabled);
        array1.add(object);

        configMain.add("TeamInfo", array1);

        try {
            FileWriter writer = new FileWriter(config);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(configMain));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static TyreConfigData getDataFromInt(int id) {
        return tyres.get(id);
    }

    private static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(reader);
    }

    public static void reloadConfig() {
        if(object != null) {
            JsonArray array = ConfigUtil.object.getAsJsonArray("Tyres");
            for (JsonElement element : array) {
                JsonObject object1 = (JsonObject) element;

                int id = object1.get("TyreID").getAsInt();
                int dura = object1.get("Dura").getAsInt();
                float degradingRate = object1.get("DegradingRate").getAsFloat();
                float steer = object1.get("Steering").getAsInt();
                int extraSpeed = object1.get("ExtraSpeed").getAsInt();
                int wetSpeed = object1.get("WetSpeed").getAsInt();


                ConfigUtil.tyres.put(id, new TyreConfigData(dura, degradingRate, steer, extraSpeed, wetSpeed));
            }

            Tyres.NULLTYRE.setData(getDataFromInt(0));
            Tyres.SOFT.setData(getDataFromInt(1));
            Tyres.MEDIUM.setData(getDataFromInt(2));
            Tyres.HARD.setData(getDataFromInt(3));
            Tyres.INTER.setData(getDataFromInt(4));
            Tyres.WET.setData(getDataFromInt(5));
            Tyres.BRIDGESTONE.setData(getDataFromInt(6));
        }
    }
}
