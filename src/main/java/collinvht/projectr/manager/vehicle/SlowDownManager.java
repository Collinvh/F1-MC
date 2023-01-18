package collinvht.projectr.manager.vehicle;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.Utils;
import collinvht.projectr.util.objects.race.RaceDriver;
import com.google.gson.*;
import lombok.Setter;
import nl.mtvehicles.core.infrastructure.helpers.VehicleData;
import nl.mtvehicles.core.infrastructure.models.Vehicle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class SlowDownManager {
    private static final HashMap<Material, Double[]> slowDowns = new HashMap<>();
    @Setter
    private static double maxSpeed = 1.0;
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
        JsonObject object = new JsonObject();
        object.addProperty("maxSpeed", maxSpeed);
        JsonArray mainObject = new JsonArray();
        slowDowns.forEach((material, doubles) -> {
            JsonObject object2 = new JsonObject();
            object2.addProperty("Material", material.name());
            object2.addProperty("SlowdownSpeed", doubles[0]);
            object2.addProperty("SteeringSpeed", doubles[1]);
            mainObject.add(object2);
        });
        object.add("array", mainObject);

        Utils.saveJSON(path, "slowdown", object);
    }

    public static void initialize() {
        File files = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/slowdown.json").toFile();
        if(files.exists()) {
            try {
                JsonObject object = (JsonObject) Utils.readJson(files.getAbsolutePath());
                maxSpeed = object.get("maxSpeed").getAsDouble();
                JsonArray array = object.getAsJsonArray("array");
                for (JsonElement jsonElement : array) {
                    JsonObject object2 = jsonElement.getAsJsonObject();
                    Material material = Material.getMaterial(object2.get("Material").getAsString());
                    Double[] doubles = new Double[2];
                    doubles[0] = object2.get("SlowdownSpeed").getAsDouble();
                    doubles[1] = object2.get("SteeringSpeed").getAsDouble();
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
                    if(VehicleData.speed.get(plate) > maxSpeed) {
                        double val = (curSpeed - (speedReduction / 73.125));
                        if (val < maxSpeed) val = maxSpeed;
                        VehicleData.speed.put(plate, val);
                    }
                }
            });
        }
    }
}
