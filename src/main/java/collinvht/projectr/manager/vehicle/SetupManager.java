package collinvht.projectr.manager.vehicle;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.Utils;
import collinvht.projectr.util.objects.Setup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

public class SetupManager {
    private static final HashMap<UUID, Setup> SETUP_HASH_MAP = new HashMap<>();

    public static void initializeSetup(UUID uuid) {
        SETUP_HASH_MAP.putIfAbsent(uuid, new Setup());
    }

    public static Setup getSetup(UUID uuid) {
        return SETUP_HASH_MAP.get(uuid);
    }

    public static void initialize() {
        File path = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/player/").toFile();
        if(path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                UUID uuid = UUID.fromString(file.getName().replace(".json", ""));
                try {
                    JsonObject object = (JsonObject) Utils.readJson(file.getAbsolutePath());
                    if(object == null) return;
                    Setup setup = Setup.fromJson(object.getAsJsonObject("setupInfo"));
                    SETUP_HASH_MAP.put(uuid, setup);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void disable() {
        File path = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/player/").toFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            if (!path.mkdirs()) {
                Files.createDirectories(Paths.get(path.toURI()));
            }
            SETUP_HASH_MAP.forEach((uuid, setup) -> {
                File file = Paths.get(path + "/" + uuid.toString() + ".json").toFile();
                JsonObject main = new JsonObject();
                main.addProperty("uuid", uuid.toString());
                main.add("setupInfo", setup.toJson());

                try {
                    FileWriter writer = new FileWriter(file);

                    writer.write(gson.toJson(main));
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
