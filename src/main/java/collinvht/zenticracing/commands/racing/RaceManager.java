package collinvht.zenticracing.commands.racing;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.racing.laptime.object.Laptime;
import collinvht.zenticracing.commands.racing.object.DRSZone;
import collinvht.zenticracing.commands.racing.object.RaceMode;
import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.object.TeamObject;
import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import collinvht.zenticracing.util.objs.Cuboid;
import collinvht.zenticracing.util.objs.DiscordUtil;
import collinvht.zenticracing.util.objs.WorldEditUtil;
import com.google.gson.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RaceManager implements CommandUtil {

    @Getter
    private static RaceObject runningRace;

    @Getter
    private static final HashMap<String, RaceObject> races = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("zentic.fia.race") || sender.hasPermission("zentic.admin")) {
                if (args.length > 0) {
                    switch (args[0]) {
                        case "create":
                            if (args.length > 2) {
                                if (races.get(args[1].toLowerCase()) != null) {
                                    sender.sendMessage(prefix + "Een race met die naam bestaat al.");
                                } else {
                                    try {
                                        int laps = Integer.parseInt(args[2]);
                                        if (laps > 0) {
                                            RaceObject newRace = new RaceObject(args[1], laps);
                                            races.put(args[1].toLowerCase(), newRace);
                                        } else {
                                            sender.sendMessage(prefix + "Je moet wel meer dan 0 laps invullen");
                                            return true;
                                        }

                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(prefix + "Error reading lapcount.");
                                        return false;
                                    }

                                    sender.sendMessage(prefix + "Race created!");

                                }
                                return true;
                            }
                        case "delete":
                            if (args.length > 1) {
                                if (races.get(args[1].toLowerCase()) != null) {
                                    RaceObject raceObject = races.get(args[1].toLowerCase());
                                    raceObject.stopRace(false);

                                    races.remove(args[1].toLowerCase());

                                    sender.sendMessage(prefix + "Race gedelete");
                                } else {
                                    sender.sendMessage(prefix + "Race bestaat niet");
                                }

                                return true;
                            }
                        case "reset":
                            if (runningRace != null) {
                                runningRace.resetRace();
                                sender.sendMessage(prefix + "Race gereset");
                                return true;
                            } else {
                                sender.sendMessage(prefix + "Er is geen race bezig");
                                return false;
                            }
                        case "result":
                            if (runningRace != null) {
                                sender.sendMessage(prefix + "Race result:");
                                runningRace.getFinishedDrivers().forEach(data -> sender.sendMessage(data.getFinishPosition() + "." + " : " + data.getDriver().getPlayer().getName()));
                            } else {
                                sender.sendMessage(prefix + "Niemand is gefinished!");
                            }
                        case "detectie":
                            if (args.length > 2) {
                                switch (args[1].toLowerCase()) {
                                    case "add":
                                        World world = BukkitAdapter.adapt(((Player) sender).getWorld());
                                        Region region = null;
                                        String id;
                                        try {
                                            region = WorldEditUtil.getSession(((Player) sender).getPlayer()).getSelection(world);
                                        } catch (IncompleteRegionException | NumberFormatException ignored) {
                                        }

                                        RaceObject obj = races.get(args[2]);
                                        if (obj != null && region != null) {
                                            if (args.length > 3) {
                                                obj.getStorage().getDetecties().put(args[3], new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                                sender.sendMessage(prefix + "Geadd!");
                                            } else {
                                                sender.sendMessage(prefix + "Usage /race detectie add [race] [naam]");
                                            }
                                        } else {
                                            sender.sendMessage(prefix + "Selectie OF race is null");
                                        }
                                        return true;
                                    case "delete":

                                        obj = races.get(args[2]);
                                        try {
                                            id = args[3];
                                            if (obj != null) {
                                                obj.getStorage().getDetecties().remove(id);
                                                sender.sendMessage(prefix + "Detectie removed.");
                                            } else {
                                                sender.sendMessage(prefix + "Race is null");
                                            }
                                        } catch (Exception e) {
                                            sender.sendMessage(prefix + args[1] + " is geen nummer.");
                                        }
                                        return true;
                                    case "list":
                                        obj = races.get(args[2]);
                                        if (obj != null) {
                                            if (obj.getStorage().getDetecties().size() > 0) {
                                                obj.getStorage().getDetecties().forEach((s, cuboid) -> {
                                                    TextComponent baseText = new TextComponent();
                                                    baseText.setText(s + " ||");
                                                    sender.spigot().sendMessage(baseText);
                                                });
                                            } else {
                                                sender.sendMessage(prefix + "Er zijn geen detecties.");
                                            }
                                        } else {
                                            sender.sendMessage(prefix + "Race is null");
                                        }

                                        return true;
                                }
                            }
                        case "drs":
                            if (args.length > 2) {
                                switch (args[1].toLowerCase()) {
                                    case "add":
                                        if(args.length > 3) {
                                            RaceObject object = races.get(args[2]);
                                            object.getStorage().getDrsZone().put(args[3].toLowerCase(), new DRSZone());
                                        }
                                    case "set":
                                        if(args.length > 4) {
                                            RaceObject object = races.get(args[2]);
                                            if(object == null) {
                                                sender.sendMessage(prefix + "Race bestaat niet.");
                                                return false;
                                            }

                                            DRSZone zone = object.getStorage().getDrsZone().get(args[3].toLowerCase());

                                            if(zone == null) {
                                                sender.sendMessage(prefix + "Die zone bestaat nog niet?");
                                                return false;
                                            }

                                            World world = BukkitAdapter.adapt(((Player) sender).getWorld());
                                            Region region = null;
                                            String id;
                                            try {
                                                region = WorldEditUtil.getSession(((Player) sender).getPlayer()).getSelection(world);
                                            } catch (IncompleteRegionException | NumberFormatException ignored) {
                                            }

                                            if(region == null) {
                                                sender.sendMessage(prefix + "Selectie is null");
                                                return false;
                                            }

                                            switch (args[4].toLowerCase()) {
                                                case "detectie":
                                                    zone.setDetectieCuboid(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                                    sender.sendMessage(prefix + "Zone aangepast.");
                                                    return true;
                                                case "straight":
                                                    zone.setDrsStraight(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                                    sender.sendMessage(prefix + "Zone aangepast.");
                                                    return true;
                                            }
                                        }
                                }
                            }
                            return true;
                        case "list":
                            if (races.size() > 0) {
                                sender.sendMessage(prefix + "Racelijst : ");
                                for (RaceObject raceObject : races.values()) {
                                    sender.sendMessage(raceObject.getRaceName() + " | = | " + raceObject.getLapCount());
                                }

                            } else {
                                sender.sendMessage(prefix + "Er zijn geen races aangemaakt");
                            }
                            return true;
                        case "createresult":
                            if(runningRace != null) {
                                if(runningRace.getRunningMode().isHasLaps()) {
                                    sender.sendMessage(prefix + "Unsuported at the moment!");
                                } else {
                                    try {
                                        createResultImage();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                sender.sendMessage(prefix + "Geen race bezig.");
                            }
                            break;
                        case "set":
                            if (args.length > 2) {
                                RaceObject obj = races.get(args[1]);
                                World world = BukkitAdapter.adapt(((Player) sender).getWorld());
                                Region region = null;
                                try {
                                    region = WorldEditUtil.getSession(((Player) sender).getPlayer()).getSelection(world);
                                } catch (IncompleteRegionException ignored) {
                                }

                                if (obj != null) {
                                    switch (args[2].toLowerCase(Locale.ROOT)) {
                                        case "finish":
                                        case "s3":
                                        case "sector3":
                                            if (region != null) {
                                                obj.getStorage().setS3(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                                sender.sendMessage(prefix + "Finish is aangepast!");
                                            } else {
                                                sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                            }
                                            return true;
                                        case "pitlane":
                                        case "pit":
                                            if (region != null) {
                                                obj.getStorage().setPit(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                                sender.sendMessage(prefix + "Pitlane is aangepast!");
                                            } else {
                                                sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                            }
                                            return true;
                                        case "s1":
                                        case "sector1":
                                            if (region != null) {
                                                obj.getStorage().setS1(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                                sender.sendMessage(prefix + "Sector 1 is aangepast!");
                                            } else {
                                                sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                            }
                                            return true;
                                        case "s2":
                                        case "sector2":
                                            if (region != null) {
                                                obj.getStorage().setS2(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                                sender.sendMessage(prefix + "Sector 2 is aangepast!");
                                            } else {
                                                sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                            }
                                            return true;
                                        case "pitexit":
                                            if (region != null) {
                                                obj.getStorage().setPitexit(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                                sender.sendMessage(prefix + "Pit Exit is aangepast!");
                                            } else {
                                                sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                            }
                                            return true;
                                        case "laps":
                                            try {
                                                int integer = Integer.parseInt(args[3]);
                                                obj.setLapCount(integer);
                                                sender.sendMessage(prefix + "Laps zijn aangepast!");
                                            } catch (NumberFormatException e) {
                                                sender.sendMessage(prefix + args[3] + " is geen nummer!");
                                            }
                                            return true;
                                    }
                                }
                            }

                            sender.sendMessage(prefix + "Usage /race set [naam] [segment] {s1/s2/s3/pit/pitexit/laps}");
                            return true;
                        case "start":
                            if (args.length > 2) {
                                RaceObject object = races.get(args[1].toLowerCase());
                                RaceMode mode = RaceMode.getModeFromString(args[2]);

                                if (runningRace != null) {
                                    runningRace.stopRace(false);
                                }

                                if (object != null) {
                                    object.startRace(mode);
                                    sender.sendMessage(prefix + "Race gestart.");
                                    runningRace = object;
                                } else {
                                    sender.sendMessage(prefix + "Race bestaat niet!");
                                }
                            } else {
                                sender.sendMessage(prefix + "/race start [naam] [id]");
                            }
                            return true;
                        case "stop":
                            if (runningRace != null) {
                                runningRace.stopRace(true);
                                runningRace = null;
                                sender.sendMessage(prefix + "Race gestopt.");
                            } else {
                                sender.sendMessage(prefix + "Er word geen race gerunned.");
                            }
                            return true;
                    }
                } else {
                    sendUsage(sender, "/race create [naam] [laps]", "/race delete [naam]", "/race reset", "/race result", "/race detectie", "/race list", "/race set", "/race start [naam] [mode]", "/race stop");
                    return true;
                }
            } else {
                sender.sendMessage(prefix + "Je hebt hier geen permissie voor.");
                return true;
            }
        } else {
            sender.sendMessage(prefix + "Op dit moment kunnen alleen spelers dit.");
            return false;
        }
        return true;
    }

    private void createResultImage() throws IOException {
        final BufferedImage image = ImageIO.read(new URL(
                "https://media.discordapp.net/attachments/634115064567431189/874392848714334258/template.png"));
        Graphics ip = image.getGraphics();

        Font basefont = new Font("Bahnschrift", Font.BOLD, 16);

        HashMap<UUID, DriverObject> drivers = DriverManager.getDrivers();
        if(drivers.values().toArray().length > 0) {
            LinkedHashMap<DriverObject, Long> sectors = new LinkedHashMap<>();

            drivers.forEach((unused, driver) -> {
                if (driver.getLapstorage().getBestTime() != null) {
                    sectors.put(driver, driver.getLapstorage().getBestTime().getLaptime());
                }
            });

            LinkedHashMap<DriverObject, Long> treeMap = SnelsteCommand.sortByValueDesc(sectors);
            if (treeMap.values().toArray().length > 0) {

                AtomicInteger pos = new AtomicInteger();
                treeMap.forEach((driver, aLong) -> {
                    if (driver.getLapstorage().getBestTime() != null) {
                        pos.getAndIncrement();
                        if (pos.get() < 21) {

                            TeamObject object = Team.checkTeamForPlayer(driver.getPlayer());

                            String team = "N/A";
                            if (object != null) {
                                team = StringUtils.capitalize(object.getTeamName());
                            }

                            if (pos.get() == 1) {

                                try {
                                    final BufferedImage character = ImageIO.read(new URL(
                                            "https://minotar.net/body/" + driver.getPlayer().getName() + "/125"));
                                    ip.drawImage(character, 132, 70, null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Font font = new Font("Calibri", Font.PLAIN, 30);
                                ip.setColor(Color.WHITE);
                                ip.setFont(font);
                                ip.drawString(driver.getPlayer().getName(), 32, 365);

                                font = new Font("Calibri", Font.PLAIN, 15);
                                ip.setFont(font);
                                ip.drawString(team, 32, 390);
                            }

                            ip.setFont(basefont);
                            ip.drawString(driver.getPlayer().getName(), 405, 78 + (23 * (pos.get() - 1)));
                            ip.setFont(basefont);
                            ip.drawString(team, 725, 78 + (23 * (pos.get() - 1)));
                            ip.setFont(basefont);
                            ip.drawString(Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getLapData().getSectorLength()), 895, 78 + (23 * (pos.get() - 1)));
                            try {
                                File file = new File(ZenticRacing.getRacing().getDataFolder() + "/storage/tyre/" + driver.getLapstorage().getBestTime().getTyre().getTyreID() + ".png");
                                Bukkit.getLogger().warning(file.toString());
                                final BufferedImage test = ImageIO.read(file);
                                ip.drawImage(test, 1033, 59 + (23 * (pos.get() - 1)), null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            ip.dispose();
            File file = new File(ZenticRacing.getRacing().getDataFolder() + "/temp/result.png");
            file.mkdir();
            ImageIO.write(image, "png", file);

            file = new File(ZenticRacing.getRacing().getDataFolder() + "/temp/result.png");

            DiscordUtil.getChannelByID(874035064692953159L).sendMessage("Result:").addFile(file).queue();
        }
    }

    public Location toLocation(org.bukkit.World world, BlockVector3 vector3) {
        return new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static void saveRaces() {
        for(RaceObject raceObj : races.values()) {
            File raceLoc = Paths.get(ZenticRacing.getRacing().getDataFolder() + "/storage/races/" + raceObj.getRaceName() + ".json").toFile();
            File path = Paths.get(ZenticRacing.getRacing().getDataFolder().toString()).toFile();
            JsonObject main = new JsonObject();

            JsonObject race = new JsonObject();
            race.addProperty("Name", raceObj.getRaceName());
            race.addProperty("Laps", raceObj.getLapCount());

            createLoc(raceObj.getStorage().getS3(), race, "Finish");
            createLoc(raceObj.getStorage().getS1(), race, "Sector1");
            createLoc(raceObj.getStorage().getS2(), race, "Sector2");
            createLoc(raceObj.getStorage().getPit(), race, "Pit");
            createLoc(raceObj.getStorage().getPitexit(), race, "PitExit");


            JsonArray array = new JsonArray();

            raceObj.getStorage().getDetecties().forEach((str, detectieZone) -> {
                JsonObject object = new JsonObject();
                object.addProperty("Naam", str);
                createLoc(detectieZone, object, "Cuboid");
                array.add(object);
            });

            race.add("Detecties", array);

            JsonArray array2 = new JsonArray();

            raceObj.getStorage().getDrsZone().forEach((str, detectieZone) -> {
                JsonObject object = new JsonObject();
                object.addProperty("Naam", str);
                createLoc(detectieZone.getDetectieCuboid(), object, "Cuboid_Detectie");
                createLoc(detectieZone.getDrsStraight(), object, "Cuboid_Straight");
                array2.add(object);
            });

            race.add("DRS", array2);

            main.add("RaceInfo", race);
            try {
                if (path.mkdir() || raceLoc.createNewFile() || raceLoc.exists()) {
                    FileWriter writer = new FileWriter(raceLoc);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    writer.write(gson.toJson(main));
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadRaces() {
        File teamLoc = Paths.get(ZenticRacing.getRacing().getDataFolder() + "/storage/races/").toFile();
        if(teamLoc.exists()) {
                File[] races = teamLoc.listFiles();
                if(races != null) {
                    for (File file : races) {
                        JsonObject jsonObject;
                        try {
                            jsonObject = (JsonObject) readJson(file.getAbsolutePath() + ".json");
                            JsonObject array = jsonObject.getAsJsonObject("RaceInfo");
                            parseRace(array);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
    }

    public static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(reader);
    }

    private static void parseRace(JsonObject race) {
        String name = race.get("Name").getAsString();
        long laps = race.get("Laps").getAsBigDecimal().longValue();
        JsonObject finish = (JsonObject) race.get("Finish");
        JsonObject sector1 = (JsonObject) race.get("Sector1");
        JsonObject sector2 = (JsonObject) race.get("Sector2");
        JsonObject pit = (JsonObject) race.get("Pit");
        JsonObject pitexit = (JsonObject) race.get("PitExit");

        JsonArray obj = race.getAsJsonArray("Detecties");

        HashMap<String, Cuboid> detectieZones = new HashMap<>();
        obj.forEach(jsonElement -> {
            JsonObject obz = jsonElement.getAsJsonObject();
            String naam = obz.get("Naam").getAsString();
            Cuboid cuboid = createCuboid(obz.get("Cuboid").getAsJsonObject());
            detectieZones.put(naam, cuboid);
        });

        JsonArray obj2 = race.getAsJsonArray("DRS");

        HashMap<String, DRSZone> drsZoneHashMap = new HashMap<>();
        obj2.forEach(jsonElement -> {
            JsonObject obz = jsonElement.getAsJsonObject();
            String naam = obz.get("Naam").getAsString();
            Cuboid cuboid = createCuboid(obz.get("Cuboid_Detectie").getAsJsonObject());
            Cuboid cuboid2 = createCuboid(obz.get("Cuboid_Straight").getAsJsonObject());
            DRSZone zone = new DRSZone();
            zone.setDrsStraight(cuboid2);
            zone.setDrsStraight(cuboid2);
            zone.setDetectieCuboid(cuboid);

            drsZoneHashMap.put(naam, zone);
        });
        RaceObject raceObj = new RaceObject(name, Math.toIntExact(laps));

        Cuboid finishcuboid = createCuboid(finish);
        Cuboid sector1cuboid = createCuboid(sector1);
        Cuboid sector2cuboid = createCuboid(sector2);
        Cuboid pitcuboid = createCuboid(pit);
        Cuboid pitexitcuboid = createCuboid(pitexit);

        if(finishcuboid.isDisabled() || sector1cuboid.isDisabled() || sector2cuboid.isDisabled() || pitcuboid.isDisabled() || pitexitcuboid.isDisabled()) {
            raceObj.setDisabled(true);
        }

        raceObj.getStorage().setS3(finishcuboid);
        raceObj.getStorage().setS1(sector1cuboid);
        raceObj.getStorage().setS2(sector2cuboid);
        raceObj.getStorage().setPit(pitcuboid);
        raceObj.getStorage().setPitexit(pitexitcuboid);
        raceObj.getStorage().setDetecties(detectieZones);

        raceObj.getStorage().setDrsZone(drsZoneHashMap);

        races.put(name.toLowerCase(), raceObj);
    }

    private static void createLoc(Cuboid cuboid, JsonObject object, String name) {
        JsonObject obj = new JsonObject();
        JsonObject loc1 = new JsonObject();
        JsonObject loc2 = new JsonObject();

        loc1.addProperty("world", cuboid.getWorld().getName());
        loc1.addProperty("x", new Double(cuboid.getLoc1().getX()).longValue());
        loc1.addProperty("y", new Double(cuboid.getLoc1().getY()).longValue());
        loc1.addProperty("z", new Double(cuboid.getLoc1().getZ()).longValue());
        obj.add("Loc1", loc1);

        loc2.addProperty("world", cuboid.getWorld().getName());
        loc2.addProperty("x", new Double(cuboid.getLoc2().getX()).longValue());
        loc2.addProperty("y", new Double(cuboid.getLoc2().getY()).longValue());
        loc2.addProperty("z", new Double(cuboid.getLoc2().getZ()).longValue());
        obj.add("Loc2", loc2);


        object.add(name, obj);
    }

    private static Cuboid createCuboid(JsonObject obj) {
        Cuboid cuboid;
        cuboid = new Cuboid(getLoc((JsonObject) obj.get("Loc1")), getLoc((JsonObject) obj.get("Loc2")));
        return cuboid;
    }

    private static Location getLoc(JsonObject obj) {
        Location location;
        long x = obj.get("x").getAsBigDecimal().longValue();
        long y = obj.get("y").getAsBigDecimal().longValue();
        long z = obj.get("z").getAsBigDecimal().longValue();
        location = new Location(Bukkit.getServer().getWorld(obj.get("world").getAsString()), x, y, z);
        return location;
    }

}
