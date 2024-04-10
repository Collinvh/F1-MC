package collinvht.f1mc.module.timetrial.command;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownIAObject;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownObject;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.timetrial.obj.TimeTrialSession;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.commands.CommandUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysql.cj.jdbc.MysqlDataSource;
import it.unimi.dsi.fastutil.Hash;
import lombok.Getter;
import me.legofreak107.vehiclesplus.VehiclesPlus;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.api.objects.spawn.SpawnMode;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.Part;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.Seat;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TimeTrialManager extends CommandUtil implements TabCompleter {
    private Gui gui;
    @Getter
    private static TimeTrialManager instance;
    private final Timer timer;
    @Getter
    private static final HashMap<UUID, String> carPreference = new HashMap<>();
    @Getter
    private static final HashMap<UUID, TimeTrialSession> sessionHashMap = new HashMap<>();

    public TimeTrialManager() {
        instance = this;
        timer = new Timer("f1mc_timetrial");
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
    }

    public void startSession(TimerTask task) {
        timer.scheduleAtFixedRate(task, 0, 1);
    }

    public void unload() {
        sessionHashMap.forEach((uuid, timeTrialSession) -> {
            timeTrialSession.setCanceled();
        });
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

        Utils.saveJSON(path, "timetrial", object);
    }

    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/timetrial", (sender, command, s, strings) -> {
            if (!(sender instanceof Player)) return prefix + "Need to be player";
            Player player = (Player) sender;
            if (TimeTrialManager.getSessionHashMap().get(player.getUniqueId()) != null)
                return prefix + "You are in a session already.";
            Window.single().setGui(this::getGui).build(player).open();
            return prefix + "Timetrial menu opened.";
        });
        addPart("car", 1, "/timetrial car [name]", ((sender, command, label, args) -> {
            if(sender instanceof Player player) {
                if (args[1].equals("f1car")) {
                    carPreference.remove(player.getUniqueId());
                } else {
                    Optional<BaseVehicle> vehicle = VehiclesPlusAPI.getInstance().getBaseVehicleFromString(args[1].toLowerCase());
                    if(vehicle.isPresent()) {
                        carPreference.put(((Player) sender).getUniqueId(), args[1].toLowerCase());
                    } else {
                        return prefix + "Invalid car name.";
                    }
                }
            }
            return prefix + "Changed Preference";
        }));
        addPart("fastest", 1, "/timetrial fastest [name] {page}", ((sender, command, label, args) -> {
            MysqlDataSource dataSource = Utils.getDatabase();
            try {
                Connection connection = dataSource.getConnection();
                int offset = 0;
                if(args.length > 2) {
                    try {
                        int offsetValue = Integer.parseInt(args[2])-1;
                        if(offsetValue > 0) offset = 5 * offsetValue;
                    } catch (NumberFormatException e) {
                        return prefix + "Invalid number";
                    }
                }
                Race race = RaceManager.getInstance().getRace(args[1]);
                if(race == null) return prefix + "That race doesnt exist";

                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `track_name`= '"+ race.getName() +"' ORDER BY `lap_length` ASC LIMIT 5 "+ (offset > 0 ? "OFFSET " + offset : "") +";");
                ResultSet rs = stmt.executeQuery();
                StringBuilder list = new StringBuilder();
                int number = offset;
                while (rs.next()) {
                    number += 1;
                    UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    list.append(number).append(". ").append(player.getName()).append(" : ").append(Utils.millisToTimeString(rs.getLong("lap_length"))).append(" Sectors: \nS1: ").append(Utils.millisToTimeString(rs.getLong("s1_length"))).append(" | S2: ").append(Utils.millisToTimeString(rs.getLong("s2_length"))).append(" | S3: ").append(Utils.millisToTimeString(rs.getLong("s3_length"))).append("\n");
                }
                if(!list.isEmpty()) {
                    return prefix + "Top 10 laps:"+ (offset != 0 ? "Page " + offset/5 : "") +"\n" + list + "=-=-=-=-=-=-=-=-=-=-=-=-=";
                } else {
                    return prefix + "No drivers in this tab";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "";
        }));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(commandSender instanceof Player) {
            ArrayList<String> list = new ArrayList<>();
            if(args.length > 1) {
                switch (args[0].toLowerCase()) {
                    case "fastest":
                        RaceManager.getRACES().forEach((s1, race) -> {
                            list.add(race.getName());
                        });
                        return list;
                    case "car":
                        VehiclesPlusAPI.getVehicleManager().getBaseVehicleMap().forEach((s1, baseVehicle) -> list.add(s1));
                        return list;
                }
            } else {
                list.add("fastest");
                list.add("car");
                list.add("reset");
            }
            return list;
        }
        return null;
    }

    public Gui getGui() {
        if(gui == null) {
            ItemStack stack = Utils.createSkull(43876, "&8Malaysia GP");
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aMalaysia GP"));
                ArrayList<String> strings = new ArrayList<>();
                strings.add(ChatColor.DARK_GRAY + "Click to start timetrial!");
                meta.setLore(strings);
                stack.setItemMeta(meta);
            }
            ItemStack stack2 = Utils.createSkull(43579, "&8Hockenheim");
            ItemMeta meta2 = stack2.getItemMeta();
            if (meta2 != null) {
                meta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eHockenheim"));
                ArrayList<String> strings = new ArrayList<>();
                strings.add(ChatColor.DARK_GRAY + "Click to start timetrial!");
                meta2.setLore(strings);
                stack2.setItemMeta(meta);
            }
            ItemStack stack3 = Utils.createSkull(43371, "&aGB");
            ItemMeta meta3 = stack3.getItemMeta();
            if (meta3 != null) {
                meta3.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aGreat Britain"));
                ArrayList<String> strings = new ArrayList<>();
                strings.add(ChatColor.DARK_GRAY + "Click to start timetrial!");
                meta3.setLore(strings);
                stack3.setItemMeta(meta);
            }
            stack.setItemMeta(meta);
            stack2.setItemMeta(meta2);
            stack3.setItemMeta(meta3);
            gui = Gui.normal().setStructure(
                    "# # # # # # # # #",
                    "# # A # B # C # #",
                    "# # # # # # # # #").addIngredient('A', new SimpleItem(stack, click -> {
                Race race = RaceManager.getInstance().getRace("sepang");
                if (race != null) {
                    Optional<BaseVehicle> baseVehicle = VehiclesPlusAPI.getInstance().getBaseVehicleFromString(carPreference.getOrDefault(click.getPlayer().getUniqueId(), "f1car"));
                    if (baseVehicle.isPresent()) {
                        SpawnedVehicle vehicle = VehiclesPlusAPI.getInstance().createVehicle(baseVehicle.get(), click.getPlayer()).spawnVehicle(race.getStorage().getTimeTrialSpawn(), SpawnMode.FORCE);
                        for (Part part : vehicle.getPartList()) {
                            if (part instanceof Seat) {
                                Seat seat = (Seat) part;
                                if (seat.getSteer()) {
                                    TimeTrialSession session = TimeTrialManager.getSessionHashMap().get(click.getPlayer().getUniqueId());
                                    if(session == null) {
                                        session = new TimeTrialSession(click.getPlayer(), click.getPlayer().getLocation(), vehicle, race);
                                        TimeTrialManager.getSessionHashMap().put(click.getPlayer().getUniqueId(), session);
                                        TimeTrialSession finalSession = session;
                                        TimerTask task = new TimerTask() {
                                            @Override
                                            public void run() {
                                                finalSession.update();
                                            }
                                        };
                                        TimeTrialManager.getInstance().startSession(task);
                                    }
                                    click.getPlayer().teleport(race.getStorage().getTimeTrialSpawn());
                                    seat.enter(click.getPlayer());
                                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        if (!onlinePlayer.getUniqueId().equals(click.getPlayer().getUniqueId())) {
                                            onlinePlayer.hidePlayer(F1MC.getInstance(), click.getPlayer());
                                            for (Part part1 : vehicle.getPartList()) {
                                                onlinePlayer.hideEntity(F1MC.getInstance(), part1.getHolder());
                                            }
                                            onlinePlayer.hideEntity(F1MC.getInstance(), vehicle.getHolder());
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            })).addIngredient('B', new SimpleItem(stack2, click -> {
                Race race = RaceManager.getInstance().getRace("hockenheim");
                if (race != null) {
                    Optional<BaseVehicle> baseVehicle = VehiclesPlusAPI.getInstance().getBaseVehicleFromString(carPreference.getOrDefault(click.getPlayer().getUniqueId(), "f1car"));
                    if (baseVehicle.isPresent()) {
                        SpawnedVehicle vehicle = VehiclesPlusAPI.getInstance().createVehicle(baseVehicle.get(), click.getPlayer()).spawnVehicle(race.getStorage().getTimeTrialSpawn(), SpawnMode.FORCE);
                        for (Part part : vehicle.getPartList()) {
                            if (part instanceof Seat) {
                                Seat seat = (Seat) part;
                                if (seat.getSteer()) {
                                    TimeTrialSession session = TimeTrialManager.getSessionHashMap().get(click.getPlayer().getUniqueId());
                                    if(session == null) {
                                        session = new TimeTrialSession(click.getPlayer(), click.getPlayer().getLocation(), vehicle, race);
                                        TimeTrialManager.getSessionHashMap().put(click.getPlayer().getUniqueId(), session);
                                        TimeTrialSession finalSession = session;
                                        TimerTask task = new TimerTask() {
                                            @Override
                                            public void run() {
                                                finalSession.update();
                                            }
                                        };
                                        TimeTrialManager.getInstance().startSession(task);
                                    }
                                    click.getPlayer().teleport(race.getStorage().getTimeTrialSpawn());
                                    seat.enter(click.getPlayer());
                                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        if (!onlinePlayer.getUniqueId().equals(click.getPlayer().getUniqueId())) {
                                            onlinePlayer.hidePlayer(F1MC.getInstance(), click.getPlayer());
                                            for (Part part1 : vehicle.getPartList()) {
                                                onlinePlayer.hideEntity(F1MC.getInstance(), part1.getHolder());
                                            }
                                            onlinePlayer.hideEntity(F1MC.getInstance(), vehicle.getHolder());
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            })).addIngredient('C', new SimpleItem(stack3, click -> {
                Race race = RaceManager.getInstance().getRace("gb");
                if (race != null) {
                    Optional<BaseVehicle> baseVehicle = VehiclesPlusAPI.getInstance().getBaseVehicleFromString(carPreference.getOrDefault(click.getPlayer().getUniqueId(), "f1car"));
                    if (baseVehicle.isPresent()) {
                        SpawnedVehicle vehicle = VehiclesPlusAPI.getInstance().createVehicle(baseVehicle.get(), click.getPlayer()).spawnVehicle(race.getStorage().getTimeTrialSpawn(), SpawnMode.FORCE);
                        for (Part part : vehicle.getPartList()) {
                            if (part instanceof Seat seat) {
                                if (seat.getSteer()) {


                                    TimeTrialManager.getSessionHashMap().put(click.getPlayer().getUniqueId(), new TimeTrialSession(click.getPlayer(), click.getPlayer().getLocation(), vehicle, race));
                                    click.getPlayer().teleport(race.getStorage().getTimeTrialSpawn());
                                    seat.enter(click.getPlayer());
                                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        if (!onlinePlayer.getUniqueId().equals(click.getPlayer().getUniqueId())) {
                                            onlinePlayer.hidePlayer(F1MC.getInstance(), click.getPlayer());
                                            for (Part part1 : vehicle.getPartList()) {
                                                onlinePlayer.hideEntity(F1MC.getInstance(), part1.getHolder());
                                            }
                                            onlinePlayer.hideEntity(F1MC.getInstance(), vehicle.getHolder());
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            })).addIngredient('#', new SimpleItem(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))).build();
        }
        return gui;
    }
}
