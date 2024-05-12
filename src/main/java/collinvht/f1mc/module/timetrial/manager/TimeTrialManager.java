package collinvht.f1mc.module.timetrial.manager;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.timetrial.object.RivalObject;
import collinvht.f1mc.module.timetrial.object.TimeTrialHolder;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysql.cj.jdbc.MysqlDataSource;
import ia.m.U;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class TimeTrialManager {
    private static final HashMap<UUID, TimeTrialHolder> timeTrialHolders = new HashMap<>();
    private static final HashMap<UUID, String> carPreference = new HashMap<>();
    private static final HashMap<String, HashMap<UUID, UUID>> rivalPreference = new HashMap<>();

    private static Gui gui;
    private static Gui gui2;
    public static String openGUI(Player player) {
        Window.single().setGui(getGui(player)).build(player).open();
        return "Time trial opened";
    }

    public static void disablePlayer(Player player, boolean force) {
        if (timeTrialHolders.containsKey(player.getUniqueId())) {
            if(!force) {
                Window window = Window.single().setGui(getGui(player)).build(player);
                timeTrialHolders.get(player.getUniqueId()).deleteVehicle();
                window.addCloseHandler(new BukkitRunnable() {
                    @Override
                    public void run() {
                        timeTrialHolders.get(player.getUniqueId()).stop();
                        timeTrialHolders.remove(player.getUniqueId());
                    }
                });
                window.open();
            } else {
                timeTrialHolders.get(player.getUniqueId()).stop();
                timeTrialHolders.remove(player.getUniqueId());
            }
        }
    }

    public static void hideAllVehicles(Player player) {
        timeTrialHolders.forEach((uuid, timeTrialHolder) -> {
            timeTrialHolder.hide(player);
        });
    }

    public static void load() {
        File files = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/timetrial.json").toFile();
        if(files.exists()) {
            try {
                JsonObject object = (JsonObject) Utils.readJson(files.getAbsolutePath());
                JsonArray array = object.getAsJsonArray("array");
                for (JsonElement jsonElement : array) {
                    JsonObject object2 = jsonElement.getAsJsonObject();
                    carPreference.put(UUID.fromString(object2.get("UUID").getAsString()), object2.get("CarName").getAsString());
                }
            } catch (Exception ignored) {
            }
        }

        File files2 = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/timetrial_rivals.json").toFile();
        if(files2.exists()) {
            try {
                JsonObject object2 = (JsonObject) Utils.readJson(files2.getAbsolutePath());
                JsonArray array2 = object2.getAsJsonArray("array");
                for (JsonElement jsonElement : array2) {
                    JsonObject object3 = jsonElement.getAsJsonObject();
                    String trackName = object3.get("TrackName").getAsString();
                    JsonArray array1 = object3.getAsJsonArray("rivals");
                    HashMap<UUID, UUID> uuiduuidHashMap = new HashMap<>();
                    for (JsonElement element : array1) {
                        JsonObject object1 = element.getAsJsonObject();
                        uuiduuidHashMap.put(UUID.fromString(object1.get("playerUUID").getAsString()), UUID.fromString(object1.get("rivalUUID").getAsString()));
                    }
                    rivalPreference.put(trackName, uuiduuidHashMap);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void unload() {
        if (!timeTrialHolders.isEmpty()) {
            timeTrialHolders.forEach((uuid, timeTrialHolder) -> timeTrialHolder.stop());
            timeTrialHolders.clear();
        }
        File path = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/").toFile();
        JsonObject object = new JsonObject();
        JsonArray mainObject = new JsonArray();
        carPreference.forEach((uuid, carName) -> {
            JsonObject object2 = new JsonObject();
            object2.addProperty("UUID", uuid.toString());
            object2.addProperty("CarName", carName);
            mainObject.add(object2);
        });
        object.add("array", mainObject);

        File path2 = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/").toFile();
        JsonObject object2 = new JsonObject();
        JsonArray mainObject2 = new JsonArray();
        rivalPreference.forEach((uuid, carName) -> {
            JsonObject object3 = new JsonObject();
            object3.addProperty("TrackName", uuid);
            JsonArray a = new JsonArray();
            carName.forEach((uuid1, uuid2) -> {
                JsonObject object1 = new JsonObject();
                object1.addProperty("playerUUID", uuid1.toString());
                object1.addProperty("rivalUUID", uuid2.toString());
                a.add(object1);
            });
            object3.add("rivals", a);
            mainObject2.add(object3);
        });
        object2.add("array", mainObject2);

        Utils.saveJSON(path2, "timetrial_rivals", object2);
        Utils.saveJSON(path, "timetrial", object);
    }

    public static Gui getGui(Player player) {
        if(timeTrialHolders.containsKey(player.getUniqueId())) {
            if (gui == null) {
                gui = Gui.normal().setStructure("# # # # # # # # #", "# # B # A # C # #", "! # # # R # # # !")
                        .addIngredient('A', createTrack("india", 43730, "&aIndia"))
                        .addIngredient('B', createTrack("gb", 44709, "&aGB"))
                        .addIngredient('C', createTrack("mexico", 43921, "&aMexico"))
                        .addIngredient('R', new SimpleItem(Utils.emptyStack(Material.RED_STAINED_GLASS_PANE), (click -> {
                            timeTrialHolders.get(click.getPlayer().getUniqueId()).stop();
                            timeTrialHolders.remove(click.getPlayer().getUniqueId());
                        })))
                        .addIngredient('!', new SimpleItem(Utils.createSkull(1223, "DLC"), (click) -> click.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&cComing soon!"))))
                        .addIngredient('#', new SimpleItem(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))).build();
            }
            return gui;
        } else {
            if (gui2 == null) {
                gui2 = Gui.normal().setStructure("# # # # # # # # #", "# # B # A # C # #", "! # # # # # # # !")
                        .addIngredient('A', createTrack("india", 43730, "&aIndia"))
                        .addIngredient('B', createTrack("gb", 44709, "&aGB"))
                        .addIngredient('C', createTrack("mexico", 43921, "&aMexico"))
                        .addIngredient('!', new SimpleItem(Utils.createSkull(1223, "DLC"), (click) -> click.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&cComing soon!"))))
                        .addIngredient('#', new SimpleItem(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))).build();
            }
            return gui2;
        }
    }

    private static SimpleItem createTrack(String track, int id, String string) {
        ItemStack stack = Utils.createSkull(id, string);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', string));
            ArrayList<String> strings = new ArrayList<>();
            strings.add(ChatColor.DARK_GRAY + "Click to start timetrial!");
            meta.setLore(strings);
            stack.setItemMeta(meta);
        }
        stack.setItemMeta(meta);
        return new SimpleItem(stack, click -> createTrackClick(click, track));
    }

    private static void createTrackClick(@NotNull Click click, String track) {
        Optional<BaseVehicle> baseVehicle = VehiclesPlusAPI.getInstance().getBaseVehicleFromString(carPreference.getOrDefault(click.getPlayer().getUniqueId(), "f1base"));
        if(baseVehicle.isPresent()) {
            Race race = RaceManager.getInstance().getRace(track);
            if(race != null) {
                Player player = click.getPlayer();
                if(timeTrialHolders.containsKey(player.getUniqueId())) {
                    timeTrialHolders.get(player.getUniqueId()).stop();
                }
                timeTrialHolders.put(player.getUniqueId(), new TimeTrialHolder(player, race, baseVehicle.get()));
            }
        } else {
            click.getPlayer().sendMessage(DefaultMessages.PREFIX + "Error loading vehicle.");
        }
    }

    public static void removeF1CarPrefrence(UUID uniqueId) {
        carPreference.remove(uniqueId);
    }

    public static void addF1CarPreference(UUID uniqueId, String car) {
        carPreference.put(uniqueId, car);
    }

    public static String setRival(UUID uniqueId, String track, String rival) {
        if(rival.equalsIgnoreCase("reset") || rival.equalsIgnoreCase("none")) {
            if(rivalPreference.get(track.toLowerCase()) == null) return "Track doesn't exist";
            rivalPreference.get(track.toLowerCase()).remove(uniqueId);
            return "Removed rival";
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(rival);
        if(player.hasPlayedBefore() || player.isOnline()) {
            if(RaceManager.getInstance().getRace(track.toLowerCase()) == null) return "Track doesn't exist";
            if(rivalPreference.get(track.toLowerCase()) == null) {
                rivalPreference.put(track, new HashMap<>());
            }
            rivalPreference.get(track.toLowerCase()).put(uniqueId, player.getUniqueId());
            return "Rival updated";
        } else {
            return "Rival doesn't exist";
        }
    }

    public static String resetLap(UUID uniqueId, String arg) {
        Race race = RaceManager.getInstance().getRace(arg.toLowerCase());
        if(race == null) return "Couldn't find that race";
        MysqlDataSource dataSource = Utils.getDatabase();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM timetrial_laps WHERE `player_uuid` = \""+ uniqueId +"\" AND `track_name` = \"" + arg.toLowerCase() + "\";");
            int update = stmt.executeUpdate();
            if(update >= 1) {
                race.updateLeaderboard();
                return "Reset your lap at " + arg.toLowerCase();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "Couldn't find your lap at " + arg.toLowerCase();
    }

    public static RivalObject getRivalObject(String track, UUID uuid) {
        HashMap<UUID, UUID> uuiduuidHashMap = rivalPreference.get(track);
        if(uuiduuidHashMap != null) {
            if(uuid != null) {
                UUID uuid2 = uuiduuidHashMap.get(uuid);
                return new RivalObject(uuid, uuid2);
            }
        }
        return null;
    }
}
