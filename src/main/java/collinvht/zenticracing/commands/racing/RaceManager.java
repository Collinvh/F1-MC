package collinvht.zenticracing.commands.racing;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.racing.object.RaceMode;
import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.commands.team.TeamBaan;
import collinvht.zenticracing.util.objs.Cuboid;
import collinvht.zenticracing.util.objs.WorldEditUtil;
import com.google.gson.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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

                                TeamBaan.getTeamBanen().forEach((s, teamBaanObject) -> {
                                    teamBaanObject.stopRace();
                                });

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

    public Location toLocation(org.bukkit.World world, BlockVector3 vector3) {
        return new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static void saveRaces() throws IOException {
        File racesLoc = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/races" + ".json").toFile();
        File path = Paths.get(ZenticRacing.getRacing().getDataFolder().toString()).toFile();
        JsonObject main = new JsonObject();
        JsonArray raceArray = new JsonArray();
        for(RaceObject raceObj : races.values()) {
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

            raceArray.add(race);
        }
        main.add("Races", raceArray);

        if (path.mkdir() || racesLoc.createNewFile() || racesLoc.exists()) {
            FileWriter writer = new FileWriter(racesLoc);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            writer.write(gson.toJson(main));
            writer.flush();
        }
    }

    public static void loadRaces() throws Exception {
        File teamLoc = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/races" + ".json").toFile();
        if(teamLoc.exists()) {
            JsonObject jsonObject = (JsonObject) readJson(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/races" + ".json");

            JsonArray array = (JsonArray) jsonObject.get("Races");
            array.forEach(team -> parseRace((JsonObject) team));
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

        RaceObject raceObj = new RaceObject(name, Math.toIntExact(laps));
        raceObj.getStorage().setS3(createCuboid(finish));
        raceObj.getStorage().setS1(createCuboid(sector1));
        raceObj.getStorage().setS2(createCuboid(sector2));
        raceObj.getStorage().setPit(createCuboid(pit));
        raceObj.getStorage().setPitexit(createCuboid(pitexit));
        raceObj.getStorage().setDetecties(detectieZones);

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
