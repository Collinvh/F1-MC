package collinvht.f1mc.module.racing.module.slowdown.manager;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownIAObject;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownObject;
import collinvht.f1mc.module.racing.object.race.RaceCar;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lone.itemsadder.api.CustomBlock;
import me.legofreak107.vehiclesplus.VehiclesPlus;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.VehicleStats;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class SlowdownManager extends ModuleBase {
    private static final HashMap<Material, SlowdownObject> slowDowns = new HashMap<>();
    private static final HashMap<String, SlowdownIAObject> customslowDowns = new HashMap<>();

    private static final Timer timer = new Timer("SlowDownTimer");


    public static String addBlock(ItemStack stack, double slowdown, double steering, double maxSpeed) {
        Material material = stack.getType();
        if(slowDowns.containsKey(material)) {
            SlowdownObject object = slowDowns.get(material);
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
        timer.cancel();
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
                    SlowdownObject slowdownObject = new SlowdownObject(material, object2.get("MaxSpeed").getAsDouble(),object2.get("SlowdownSpeed").getAsDouble(),object2.get("SteeringSpeed").getAsDouble());
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
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                HashMap<UUID, RaceDriver> cloned = (HashMap<UUID, RaceDriver>) VPListener.getRACE_DRIVERS().clone();
                HashMap<UUID, RaceCar> cloned2 = (HashMap<UUID, RaceCar>) VPListener.getRACE_CARS().clone();
                cloned.forEach((uuid, raceDriver) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(raceDriver.getVehicle() != null) {
                                if(raceDriver.isDriving()) {
                                    update(raceDriver);
                                    if(raceDriver.getVehicle() != null) {
                                        if (Bukkit.getPlayer(raceDriver.getDriverUUID()) != null) {
                                            SpawnedVehicle spawnedVehicle = raceDriver.getVehicle();
                                            Bukkit.getPlayer(raceDriver.getDriverUUID()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Speed: " + spawnedVehicle.getCurrentSpeedInKm() + " | Fuel: " + spawnedVehicle.getStorageVehicle().getVehicleStats().getCurrentFuel()));
                                        }
                                    }
                                }
                            }
                        }
                    }.run();
                });
                cloned2.forEach((uuid, car) -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        car.updateTyre();
                    }
                }.run());
            }
        }, 0, 1);
    }

    public static void update(RaceDriver raceDriver) {
        Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
        if(player != null && player.isOnline()) {
            SpawnedVehicle vehicle = raceDriver.getVehicle();
            VehicleStats stats = vehicle.getStorageVehicle().getVehicleStats();
            Block block = player.getLocation().clone().add(0, -0.2, 0).getBlock();
            if(block.getBlockData() instanceof Slab) {
                block = player.getLocation().clone().add(0, -1.2, 0).getBlock();
            }
            float steering = vehicle.getBaseVehicle().getTurningRadiusSettings().getLowSpeed();
            float steeringHigh = vehicle.getBaseVehicle().getTurningRadiusSettings().getHighSpeed();
            float acceleration = vehicle.getBaseVehicle().getAccelerationSettings().getLowSpeed();
            float accelerationHigh = vehicle.getBaseVehicle().getAccelerationSettings().getHighSpeed();
            float braking = vehicle.getBaseVehicle().getBrakeSettings().getBase();
            if(block.getType() == Material.NOTE_BLOCK) {
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                if(customBlock != null) {
                    if (customslowDowns.get(customBlock.getNamespacedID()) != null) {
                        SlowdownIAObject obj = customslowDowns.get(customBlock.getNamespacedID());
                        steering *= (float) obj.getSteeringPercent();
                        steeringHigh *= (float) obj.getSteeringPercent();
                        acceleration *= (float) (obj.getSteeringPercent()*2);
                        accelerationHigh *= (float) (obj.getSteeringPercent()*2);
                        braking *= (float) (obj.getSteeringPercent()*2);
                    }
                }
            } else if (slowDowns.containsKey(block.getType())) {
                SlowdownObject obj = slowDowns.get(block.getType());
                steering *= (float) obj.getSteeringPercent();
                steeringHigh *= (float) obj.getSteeringPercent();
                acceleration *= (float) (obj.getSteeringPercent()*2);
                accelerationHigh *= (float) (obj.getSteeringPercent()*2);
                braking *= (float) (obj.getSteeringPercent()*2);
            }
            stats.setLowSpeedSteering(steering);
            stats.setHighSpeedSteering(steeringHigh);
            stats.setLowSpeedAcceleration(acceleration);
            stats.setHighSpeedAcceleration(accelerationHigh);
            stats.setBrakeForce(braking);
        }
    }
}
