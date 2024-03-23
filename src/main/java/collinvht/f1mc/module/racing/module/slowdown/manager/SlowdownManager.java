package collinvht.f1mc.module.racing.module.slowdown.manager;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownObject;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.VehicleStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class SlowdownManager extends ModuleBase {
    private static final HashMap<Material, SlowdownObject> slowDowns = new HashMap<>();
    @Setter
    private static double maxSpeed = 1.0;

    private static int vehicleRunnable;


    public static String addBlock(ItemStack stack, double slowdown, double steering, double maxSpeed) {
        Material material = stack.getType();
        if(slowDowns.containsKey(material)) {
            SlowdownObject object = slowDowns.get(material);
            object.setSlowdownSpeed(slowdown);
            object.setSteeringPercent(steering);
            object.setMaxSpeedPercent(maxSpeed);
        } else {
            slowDowns.put(material, new SlowdownObject(material, maxSpeed,slowdown,steering));
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
        object.addProperty("maxSpeed", maxSpeed);
        JsonArray mainObject = new JsonArray();
        slowDowns.forEach((material, doubles) -> {
            JsonObject object2 = new JsonObject();
            object2.addProperty("Material", material.name());
            object2.addProperty("SlowdownSpeed", doubles.getSlowdownSpeed());
            object2.addProperty("SteeringSpeed", doubles.getSteeringPercent());
            object2.addProperty("MaxSpeed", doubles.getMaxSpeedPercent());
            mainObject.add(object2);
        });
        object.add("array", mainObject);

        Utils.saveJSON(path, "slowdown", object);
        Bukkit.getScheduler().cancelTask(vehicleRunnable);
    }

    public void load() {
        File files = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/slowdown.json").toFile();
        if(files.exists()) {
            try {
                JsonObject object = (JsonObject) Utils.readJson(files.getAbsolutePath());
                maxSpeed = object.get("maxSpeed").getAsDouble();
                JsonArray array = object.getAsJsonArray("array");
                for (JsonElement jsonElement : array) {
                    JsonObject object2 = jsonElement.getAsJsonObject();
                    Material material = Material.getMaterial(object2.get("Material").getAsString());
                    SlowdownObject slowdownObject = new SlowdownObject(material, object2.get("MaxSpeed").getAsDouble(),object2.get("SlowdownSpeed").getAsDouble(),object2.get("SteeringSpeed").getAsDouble());
                    slowDowns.put(material, slowdownObject);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        vehicleRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                VPListener.getRACE_DRIVERS().forEach((uuid, raceDriver) -> {
                    if(raceDriver.getVehicle() != null) {
                        if(raceDriver.isDriving()) {
                            update(raceDriver);
                        }
                    }

                });
            }
        }.runTaskTimer(F1MC.getInstance(), 0, 0).getTaskId();

    }

    public static void update(RaceDriver raceDriver) {
        Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
        if(player != null && player.isOnline()) {
            SpawnedVehicle vehicle = raceDriver.getVehicle();
            VehicleStats stats = vehicle.getStorageVehicle().getVehicleStats();
            double curSpeed = stats.getCurrentSpeed();
            Block block = player.getLocation().clone().add(0, -0.2, 0).getBlock();
            if(block.getBlockData() instanceof Slab) {
                block = player.getLocation().clone().add(0, -1.2, 0).getBlock();
            }
            Block finalBlock = block;
            slowDowns.forEach((material, obj) -> {
                double speedReduction = obj.getSlowdownSpeed();
                if(finalBlock.getType().equals(material)) {
                    if(curSpeed > maxSpeed) {
                        double val = (curSpeed - (speedReduction));
                        if (val < maxSpeed) val = maxSpeed;
                        stats.setCurrentSpeed(val);
                    }
                }
            });
        }
    }
}
