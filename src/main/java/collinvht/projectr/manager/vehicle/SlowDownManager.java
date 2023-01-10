package collinvht.projectr.manager.vehicle;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.JSONUtil;
import collinvht.projectr.util.objects.race.RaceDriver;
import com.google.gson.*;
import nl.mtvehicles.core.infrastructure.helpers.VehicleData;
import nl.mtvehicles.core.infrastructure.models.Vehicle;
import nl.mtvehicles.core.infrastructure.models.VehicleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class SlowDownManager {
    private static final HashMap<Material, Double[]> slowDowns = new HashMap<>();
    public static String addBlock(ItemStack stack, double slowdownSpeed, double steering) {
        Double[] strs = new Double[2];
        strs[0] = slowdownSpeed;
        strs[1] = steering;
        slowDowns.put(stack.getType(), strs);
        return "Block Geadd";
    }

    public static String removeBlock(ItemStack stack) {
        if(slowDowns.containsKey(stack.getType())) {
            slowDowns.remove(stack.getType());
            return "Block geremoved";
        } else {
            return "Block staat niet in de slowdownlist";
        }
    }

    public static void disable() {
        File path = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/").toFile();
        File file = Paths.get(path + "/slowdown.json").toFile();
        JsonArray mainObject = new JsonArray();
        slowDowns.forEach((material, doubles) -> {
            JsonObject object = new JsonObject();
            object.addProperty("Material", material.name());
            object.addProperty("SlowdownSpeed", doubles[0]);
            object.addProperty("SteeringSpeed", doubles[1]);
            mainObject.add(object);
        });

        try {
            if(!path.mkdirs()) {
                Files.createDirectories(Paths.get(path.toURI()));
            }
            FileWriter writer = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(mainObject));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize() {
        File files = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/slowdown.json").toFile();
        if(files.exists()) {
            try {
                JsonArray array = (JsonArray) JSONUtil.readJson(files.getAbsolutePath());
                for (JsonElement jsonElement : array) {
                    JsonObject object = jsonElement.getAsJsonObject();
                    Material material = Material.getMaterial(object.get("Material").getAsString());
                    Double[] doubles = new Double[2];
                    doubles[0] = object.get("SlowdownSpeed").getAsDouble();
                    doubles[1] = object.get("SteeringSpeed").getAsDouble();
                    slowDowns.put(material, doubles);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void update(RaceDriver raceDriver) {
        Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
        if(player != null && player.isOnline()) {
            Vehicle vehicle = raceDriver.getVehicle();
            String plate = vehicle.getLicensePlate();
            double curSpeed = VehicleData.speed.get(plate);
            Block block = player.getLocation().clone().add(0, -0.8, 0).getBlock();
            slowDowns.forEach((material, doubles) -> {
                double speedReduction = doubles[0];
                if(block.getType().equals(material)) {
                    if(VehicleData.speed.get(plate) > 0.15) {
                        double val = (curSpeed - (speedReduction / 73.125));
                        if (val < 0) val = 0;
                        VehicleData.speed.put(plate, val);
                    }
                }
            });
        }
    }
}
