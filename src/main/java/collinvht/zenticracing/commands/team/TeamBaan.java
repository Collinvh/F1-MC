package collinvht.zenticracing.commands.team;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.racing.RaceManager;
import collinvht.zenticracing.commands.racing.object.RaceMode;
import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.commands.team.object.TeamBaanObject;
import collinvht.zenticracing.commands.team.object.TeamObject;
import collinvht.zenticracing.util.objs.Cuboid;
import collinvht.zenticracing.util.objs.WorldEditUtil;
import com.google.gson.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.api.objects.spawn.SpawnMode;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.StorageVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

public class TeamBaan implements CommandUtil {

    @Getter
    private static final HashMap<String, TeamBaanObject> teamBanen = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {

            TeamObject object = Team.checkTeamForPlayer(((Player) sender).getPlayer());
            if (!sender.hasPermission("zentic.team")) {
                if (object != null) {
                    TeamBaanObject baan = teamBanen.get(object.getTeamName().toLowerCase());
                    if (baan != null) {
                        if (args.length > 0) {
                            switch (args[0]) {
                                case "start":
                                    if (RaceManager.getRunningRace() != null) {
                                        sender.sendMessage(prefix + "Je kunt niet rijden op je baan zolang er een officiele sessie bezig is!");
                                        return false;
                                    }

                                    baan.getObject().startRace(RaceMode.TRAINING_TEAM);
                                    sender.sendMessage(prefix + "Race gestart!");
                                    return true;
                                case "stop":
                                    baan.getObject().stopRace(false);
                                    sender.sendMessage(prefix + "Race gestopt!");
                                    return true;
                                case "spawncar":
                                    if (args.length > 1) {
                                        try {
                                            int id = Integer.parseInt(args[1]);

                                            BaseVehicle base = baan.getBaseVehicle();
                                            StorageVehicle vehicle = baan.getVehicle();

                                            if (id == 2) {
                                                vehicle = baan.getVehicle2();
                                            }

                                            if (vehicle == null) {
                                                vehicle = VehiclesPlusAPI.getInstance().createVehicle(base, ((Player) sender).getPlayer());
                                                if (id == 2) {
                                                    baan.setVehicle2(vehicle);
                                                } else {
                                                    baan.setVehicle(vehicle);
                                                }
                                            }

                                            if (vehicle.getSpawnedVehicle() != null) {
                                                sender.sendMessage(prefix + "Auto is al gespawned!");
                                            } else {
                                                vehicle.spawnVehicle(baan.getCarSpawnLocation(), SpawnMode.FORCE);
                                                sender.sendMessage(prefix + "Auto gespawned!");
                                            }

                                        } catch (NumberFormatException e) {
                                            sender.sendMessage(prefix + "Dat nummer bestaat niet?");
                                        }
                                    } else {
                                        sender.sendMessage(prefix + "Geef aan welke car je wilt spawnen!");
                                    }
                                    return true;
                                case "despawncar":
                                    if (args.length > 1) {
                                        try {
                                            int id = Integer.parseInt(args[1]);

                                            StorageVehicle vehicle = baan.getVehicle();

                                            if (id == 2) {
                                                vehicle = baan.getVehicle2();
                                            }

                                            if (vehicle.getSpawnedVehicle() == null) {
                                                sender.sendMessage(prefix + "Auto is niet gespawned!");
                                            } else {
                                                vehicle.getSpawnedVehicle().despawn(true);
                                                VehiclesPlusAPI.getInstance().removeVehicle(vehicle);
                                                sender.sendMessage(prefix + "Auto gedespawned!");
                                            }

                                        } catch (NumberFormatException e) {
                                            sender.sendMessage(prefix + "Dat nummer bestaat niet?");
                                        }
                                    } else {
                                        sender.sendMessage(prefix + "Geef aan welke car je wilt despawnen!");
                                    }
                                    return true;
                                case "reset":
                                    baan.getObject().resetRace();
                                    sender.sendMessage(prefix + "Race gereset!");
                                    return true;
                            }
                        } else {
                            sendUsage(sender, "/teambaan start", "/teambaan spawncar [1/2]", "/teambaan despawncar [1/2]", "/teambaan stop", "/teambaan reset");
                        }
                        if (((Player) sender).getWorld() == baan.getCarSpawnLocation().getWorld()) {

                        } else {
                            sender.sendMessage(prefix + "Je zit niet in dezelfde wereld als jouw circuit!");
                        }
                    } else {
                        sender.sendMessage(prefix + "Jouw team heeft geen baan!");
                    }
                }
            } else {
                if (args.length > 0) {
                    switch (args[0]) {
                        case "create":
                            if (args.length > 1) {
                                TeamObject teamObject = Team.getTeamObj().get(args[1].toLowerCase());
                                if (teamObject != null) {
                                    TeamBaanObject teamBaan = new TeamBaanObject(new RaceObject(teamObject.getTeamName(), 0), null, teamObject);
                                    teamBanen.put(teamObject.getTeamName().toLowerCase(), teamBaan);
                                } else {
                                    sender.sendMessage(prefix + "Dat team bestaat niet");
                                }
                            }
                            return true;
                        case "setcar":
                            if (args.length > 2) {
                                TeamObject teamObject = Team.getTeamObj().get(args[1].toLowerCase());
                                if (teamObject != null) {
                                    TeamBaanObject teamBaanObject = teamBanen.get(teamObject.getTeamName().toLowerCase());
                                    if (teamBaanObject != null) {
                                        BaseVehicle vehicle = VehiclesPlusAPI.getVehicleManager().getBaseVehicleMap().get(args[2]);
                                        if (vehicle != null) {
                                            teamBaanObject.setBaseVehicle(vehicle);
                                        } else {
                                            sender.sendMessage(prefix + "Die auto word niet herkent");
                                        }
                                    } else {
                                        sender.sendMessage(prefix + "Dat team heeft geen teambaan!");
                                    }
                                } else {
                                    sender.sendMessage(prefix + "Dat team bestaat niet");
                                }
                            } else {
                                sendUsage(sender, "/teambaan setcar [team] [naam]");
                            }
                        case "set":
                            TeamObject teamObject = Team.getTeamObj().get(args[1]);
                            if (teamObject != null) {
                                TeamBaanObject obj = teamBanen.get(teamObject.getTeamName().toLowerCase());

                                if(obj == null) {
                                    sender.sendMessage(prefix + "Dat team heeft geen teambaan!");
                                    return false;
                                }

                                World world = BukkitAdapter.adapt(((Player) sender).getWorld());
                                Region region = null;
                                try {
                                    region = WorldEditUtil.getSession(((Player) sender).getPlayer()).getSelection(world);
                                } catch (IncompleteRegionException ignored) {
                                }

                                switch (args[2].toLowerCase()) {
                                    case "spawnloc":
                                        obj.setCarSpawnLocation(((Player) sender).getLocation());
                                        sender.sendMessage(prefix + "Locatie aangepast!");
                                        return true;
                                    case "finish":
                                    case "s3":
                                    case "sector3":
                                        if (region != null) {
                                            obj.getObject().getStorage().setS3(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                            sender.sendMessage(prefix + "Finish is aangepast!");
                                        } else {
                                            sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                        }
                                        return true;
                                    case "pitlane":
                                    case "pit":
                                        if (region != null) {
                                            obj.getObject().getStorage().setPit(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                            sender.sendMessage(prefix + "Pitlane is aangepast!");
                                        } else {
                                            sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                        }
                                        return true;
                                    case "s1":
                                    case "sector1":
                                        if (region != null) {
                                            obj.getObject().getStorage().setS1(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                            sender.sendMessage(prefix + "Sector 1 is aangepast!");
                                        } else {
                                            sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                        }
                                        return true;
                                    case "s2":
                                    case "sector2":
                                        if (region != null) {
                                            obj.getObject().getStorage().setS2(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                            sender.sendMessage(prefix + "Sector 2 is aangepast!");
                                        } else {
                                            sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                        }
                                        return true;
                                    case "pitexit":
                                        if (region != null) {
                                            obj.getObject().getStorage().setPitexit(new Cuboid(toLocation(((Player) sender).getWorld(), region.getMinimumPoint()), toLocation(((Player) sender).getWorld(), region.getMaximumPoint())));
                                            sender.sendMessage(prefix + "Pit Exit is aangepast!");
                                        } else {
                                            sender.sendMessage(prefix + "Je moet wel iets geselecteerd hebben");
                                        }
                                        return true;
                                    case "laps":
                                        try {
                                            int integer = Integer.parseInt(args[3]);
                                            obj.getObject().setLapCount(integer);
                                            sender.sendMessage(prefix + "Laps zijn aangepast!");
                                        } catch (NumberFormatException e) {
                                            sender.sendMessage(prefix + args[3] + " is geen nummer!");
                                        }
                                        return true;
                                }
                            }
                    }
                } else {
                    sendUsage(sender, "/teambaan create [team]", "/teambaan setcar [team] [naam]", "/teambaan set [team] [part]", "/teambaan start [team]", "/teambaan stop [team]", "/teambaan reset [team]", "/teambaan delete [team]");
                }
            }
        } else {
            sender.sendMessage(prefix + "Dit werkt voor nu alleen voor spelers!");
        }
        return true;
    }

    public Location toLocation(org.bukkit.World world, BlockVector3 vector3) {
        return new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static void saveRaces() {
        File racesLoc = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/teambanen" + ".json").toFile();
        File path = Paths.get(ZenticRacing.getRacing().getDataFolder().toString()).toFile();
        JsonObject main = new JsonObject();
        JsonArray raceArray = new JsonArray();
        for(TeamBaanObject raceObj : teamBanen.values()) {
            if(raceObj.getBaseVehicle() == null || raceObj.getObject().getRaceName() == null || raceObj.getObject().getStorage().hasNull() || raceObj.getCarSpawnLocation() == null) {
                return;
            }

            JsonObject race = new JsonObject();
            race.addProperty("TeamNaam", raceObj.getTeam().getTeamName());
            race.addProperty("BaseVehicleName", raceObj.getBaseVehicle().getName());
            race.addProperty("Name", raceObj.getObject().getRaceName());

            createLoc(raceObj.getObject().getStorage().getS3(), race, "Finish");
            createLoc(raceObj.getObject().getStorage().getS1(), race, "Sector1");
            createLoc(raceObj.getObject().getStorage().getS2(), race, "Sector2");
            createLoc(raceObj.getObject().getStorage().getPit(), race, "Pit");
            createLoc(raceObj.getObject().getStorage().getPitexit(), race, "PitExit");

            JsonObject spawnLoc = new JsonObject();

            spawnLoc.addProperty("world", Objects.requireNonNull(raceObj.getCarSpawnLocation().getWorld()).getName());
            spawnLoc.addProperty("x", raceObj.getCarSpawnLocation().getX());
            spawnLoc.addProperty("y", raceObj.getCarSpawnLocation().getY());
            spawnLoc.addProperty("z", raceObj.getCarSpawnLocation().getZ());
            spawnLoc.addProperty("yaw", raceObj.getCarSpawnLocation().getYaw());
            spawnLoc.addProperty("pitch", raceObj.getCarSpawnLocation().getPitch());

            race.add("spawnLoc", spawnLoc);


            JsonArray array = new JsonArray();

            raceObj.getObject().getStorage().getDetecties().forEach((str, detectieZone) -> {
                JsonObject object = new JsonObject();
                object.addProperty("Naam", str);
                createLoc(detectieZone, object, "Cuboid");
                array.add(object);
            });

            race.add("Detecties", array);

            raceArray.add(race);
        }
        main.add("Races", raceArray);

        try {
            if (path.mkdir() || racesLoc.createNewFile() || racesLoc.exists()) {
                FileWriter writer = new FileWriter(racesLoc);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                writer.write(gson.toJson(main));
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadRaces() {
        File teamLoc = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/teambanen" + ".json").toFile();
        if(teamLoc.exists()) {
            JsonObject jsonObject = null;
            try {
                jsonObject = (JsonObject) readJson(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/teambanen" + ".json");
                JsonArray array = (JsonArray) jsonObject.get("Races");
                array.forEach(team -> parseRace((JsonObject) team));
            } catch (Exception e) {
                e.printStackTrace();
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
        TeamObject team = Team.getTeamObj().get(race.get("TeamNaam").getAsString().toLowerCase());
        if(team != null) {
            JsonObject finish = (JsonObject) race.get("Finish");
            String baseVehicle = race.get("BaseVehicleName").getAsString();
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

            RaceObject raceObj = new RaceObject(name, Math.toIntExact(0));
            raceObj.getStorage().setS3(createCuboid(finish));
            raceObj.getStorage().setS1(createCuboid(sector1));
            raceObj.getStorage().setS2(createCuboid(sector2));
            raceObj.getStorage().setPit(createCuboid(pit));
            raceObj.getStorage().setPitexit(createCuboid(pitexit));
            raceObj.getStorage().setDetecties(detectieZones);

            BaseVehicle vehicle = VehiclesPlusAPI.getVehicleManager().getBaseVehicleMap().get(baseVehicle);

            Location spawnLoc = getLoc(race.get("spawnLoc").getAsJsonObject());

            TeamBaanObject object = new TeamBaanObject(raceObj, spawnLoc, team);
            object.setBaseVehicle(vehicle);

            teamBanen.put(team.getTeamName().toLowerCase(), object);
        }
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

        if(obj.get("yaw") != null && obj.get("pitch") != null) {
            long yaw = obj.get("yaw").getAsBigDecimal().longValue();
            long pitch = obj.get("pitch").getAsBigDecimal().longValue();

            location.setYaw(yaw);
            location.setPitch(pitch);
        }

        return location;
    }
}
