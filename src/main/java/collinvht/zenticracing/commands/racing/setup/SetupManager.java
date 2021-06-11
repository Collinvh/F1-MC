package collinvht.zenticracing.commands.racing.setup;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.racing.setup.obj.SetupOBJ;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class SetupManager {

    private static final HashMap<UUID, SetupOBJ> setups = new HashMap<>();

    public static void createSetupForPlayer(UUID uuid) {
        if(!setups.containsKey(uuid)) {
            setups.put(uuid, new SetupOBJ(uuid));
        }
    }

    public static void setSetup(UUID uuid, SetupOBJ obj) {
        setups.put(uuid, obj);
    }

    public static void deleteSetup(UUID uuid) {
        setups.remove(uuid);
    }

    public static SetupOBJ getSetup(UUID uuid) {
        return setups.get(uuid);
    }


    public static void saveSetups() {
        setups.forEach((uuid, obj) -> {
            File setupLoc = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/player/" + uuid.toString() + ".json").toFile();
            JsonObject main = new JsonObject();
            main.addProperty("uuid", uuid.toString());

            JsonObject setupInfo = new JsonObject();

            setupInfo.addProperty("frontWingAngle", obj.getFrontWingAngle().getInteger());
            setupInfo.addProperty("rearWingAngle", obj.getRearWingAngle().getInteger());

            setupInfo.addProperty("frontCamber", obj.getFrontCamber().getAFloat());
            setupInfo.addProperty("rearCamber", obj.getRearCamber().getAFloat());
            setupInfo.addProperty("frontToe", obj.getFrontToe().getAFloat());
            setupInfo.addProperty("rearToe", obj.getRearToe().getAFloat());

            setupInfo.addProperty("frontRideHeight", obj.getFrontRideHeight().getInteger());
            setupInfo.addProperty("rearRideHeight", obj.getRearRideHeight().getInteger());

            setupInfo.addProperty("brakePressure", obj.getBrakePressure().getInteger());
            setupInfo.addProperty("brakeBias", obj.getBrakeBias().getInteger());

            main.add("setupInfo", setupInfo);

            try {
                if (setupLoc.createNewFile() || setupLoc.exists()) {
                    FileWriter writer = new FileWriter(setupLoc);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    writer.write(gson.toJson(main));
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void loadSetups() {
        File playerStorage = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/player/").toFile();
        if(playerStorage.exists()) {
            ArrayList<File> files = new ArrayList<>(Arrays.asList(playerStorage.listFiles()));
            files.forEach(file -> {
                UUID uuid = UUID.fromString(file.getName().replace(".json", ""));
                SetupOBJ obj = new SetupOBJ(uuid);

                try {
                    JsonObject object = (JsonObject) readJson(file.getAbsolutePath());
                    if (object != null) {
                        JsonObject info = object.getAsJsonObject("setupInfo");

                        obj.getFrontWingAngle().setValue(info.get("frontWingAngle").getAsInt());
                        obj.getRearWingAngle().setValue(info.get("rearWingAngle").getAsInt());

                        obj.getFrontCamber().setValue(info.get("frontCamber").getAsFloat());
                        obj.getRearCamber().setValue(info.get("rearCamber").getAsFloat());
                        obj.getFrontToe().setValue(info.get("frontToe").getAsFloat());
                        obj.getRearToe().setValue(info.get("rearToe").getAsFloat());

                        obj.getFrontRideHeight().setValue(info.get("frontRideHeight").getAsInt());
                        obj.getRearRideHeight().setValue(info.get("rearRideHeight").getAsInt());

                        obj.getBrakePressure().setValue(info.get("brakePressure").getAsInt());
                        obj.getBrakeBias().setValue(info.get("brakeBias").getAsInt());

                        setups.put(uuid, obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(reader);
    }
}
