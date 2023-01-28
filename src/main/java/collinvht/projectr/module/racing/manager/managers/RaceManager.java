package collinvht.projectr.module.racing.manager.managers;

import collinvht.projectr.ProjectR;
import collinvht.projectr.module.racing.object.NamedCuboid;
import collinvht.projectr.module.racing.util.RacingMessages;
import collinvht.projectr.module.racing.object.race.RaceListener;
import collinvht.projectr.util.Utils;
import collinvht.projectr.module.racing.object.race.Race;
import collinvht.projectr.util.DefaultMessages;
import collinvht.projectr.util.modules.ModuleBase;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class RaceManager extends ModuleBase {
    private static RaceManager instance;

    private static final HashMap<String, Race> RACES = new HashMap<>();

    public RaceManager() {
        RaceListener.initialize();
    }

    public static RaceManager getInstance() {
        if(instance == null) {
            instance = new RaceManager();
        }
        return instance;
    }

    @Override
    public void load() {
        loadRaces();
    }

    @Override
    public void saveModule() {
        saveRaces();
    }

    private void saveRaces() {
        if(!RACES.isEmpty()) {
            RACES.forEach((s, race) ->  {
                race.saveJson();
            });
        }
        RaceListener.stopListening();
    }

    private void loadRaces() {
        File raceFiles = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/races/").toFile();
        if(raceFiles.exists()) {
            File[] races = raceFiles.listFiles();
            if(races != null) {
                for (File raceFile : races) {
                    try {
                        Race race = Race.createRaceFromJson((JsonObject) Utils.readJson(raceFile.getAbsolutePath()));
                        if(race != null) {
                            RACES.put(race.getName(), race);
                        }
                    } catch (Exception e) {
                        Bukkit.getLogger().warning(raceFile.getAbsolutePath() + " failed to load.");
                    }
                }
            }
        }
    }

    public String startRace(String raceName, String mode) {
        if(!raceExists(raceName)) return RacingMessages.RACE_DOES_NOT_EXIST;
        try {
            int modeInt = Integer.parseInt(mode);
            return RaceListener.startListeningTo(getRace(raceName), modeInt);
        } catch (NumberFormatException e) {
            return DefaultMessages.INVALID_NUMBER;
        }
    }

    public String stopRace(String name) {
        Race race = RACES.get(name);
        return RaceListener.stopListeningTo(race);
    }

    public String deleteRace(String raceName) {
        if(raceExists(raceName)) {
            Race race = RACES.get(raceName);
            if(RaceListener.isListeningToRace(race)) {
                RaceListener.stopListeningTo(race);
            }

            RACES.remove(raceName);
            return RacingMessages.RACE_DELETED;
        } else {
            return RacingMessages.RACE_DOES_NOT_EXIST;
        }
    }

    public String getRaceResult(String raceName, String type, UUID playerUUID) {
        Race race = RACES.get(raceName);
        if(race == null) return DefaultMessages.PREFIX + "Race doesn't exist.";
        if(!RaceListener.isListeningToRace(race)) return RacingMessages.NO_ONGOING_RACE;
        switch (type) {
            case "timing":
            case "fastest": {
//                HashMap<UUID, RaceDriver> drivers = MTListener.getRaceDrivers();
//                if(drivers.values().toArray().length > 0) {
//                    LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();
//                    drivers.forEach((unused, driver) -> {
//                        if (driver.getLaptimes().getFastestLap() != null) {
//                            sectors.put(driver, driver.getLaptimes().getFastestLap().getLaptime());
//                        }
//                    });
//
//                    LinkedHashMap<RaceDriver, Long> treeMap = Utils.sortByValueDesc(sectors);
//                    if (treeMap.values().toArray().length > 0) {
//                        StringBuilder builder = new StringBuilder();
//                        builder.append(RacingMessages.FASTEST_LAPS);
//                        AtomicInteger pos = new AtomicInteger();
//                        treeMap.forEach((driver, aLong) -> {
//                            TextComponent component = new TextComponent();
//                            OfflinePlayer player = Bukkit.getOfflinePlayer(driver.getDriverUUID());
//                            pos.getAndIncrement();
//                            builder.append(pos.get()).append(". ").append(player.getName()).append(" ").append(Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getLapData().getSectorLength())).append("\n");
////                            component.setText(pos.get() + ". " + player.getName() + " " + Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getLapData().getSectorLength()));
////                            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getS1data().getSectorLength()) + " | " + Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getS2data().getSectorLength()) + " | " + Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getS3data().getSectorLength()))));
////                            builder.append(component).append("\n");
//                        });
//                        return builder.toString();
//                    }
//                }
                return RacingMessages.NO_LAPS_DRIVEN;
            }
            case "result": {
//                if(RaceListener.getInstance().getFinishers().size() > 0) {
//                    StringBuilder builder = new StringBuilder();
//                    builder.append(RacingMessages.RACE_RESULT);
//                    RaceListener.getInstance().getFinishers().forEach((integer, uuid) -> {
//                        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
//                        builder.append(integer).append(". | ").append(player.getName()).append("\n");
//                    });
//                    return builder.toString();
//                } else {
                    return RacingMessages.NO_FINISHERS;
//                }
            }
            case "position": {
                StringBuilder builder = new StringBuilder();
//                HashMap<UUID, RaceDriver> drivers = MTListener.getRaceDrivers();
//                if(drivers.size() > 0) {
//                    builder.append(RacingMessages.RACE_POSITION);
//
//                    LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();
//
//                    drivers.forEach((unused, driver) -> sectors.put(driver, (long) driver.getLaptimes().getSectors()));
//
//                    LinkedHashMap<RaceDriver, Long> treeMap = Utils.sortByValueDesc(sectors);
//
//
//                    if (treeMap.size() > 0) {
//                        builder.append(RacingMessages.SECTOR_POSITION);
//
//                        AtomicInteger pos = new AtomicInteger();
//                        treeMap.forEach((driver, integer) -> {
//                            if (integer > 0) {
//                                OfflinePlayer player = Bukkit.getOfflinePlayer(driver.getDriverUUID());
//                                builder.append(pos.incrementAndGet()).append(". ").append(player.getName()).append(" : ").append(integer).append("\n");
//                            }
//                        });
//                        return builder.toString();
//                    }
//                } else {
                    return RacingMessages.NO_LAPS_DRIVEN;
//                }
            }
            case "record": {
//                StringBuilder builder = new StringBuilder();
//                RaceDriver driver = MTListener.getRaceDrivers().get(playerUUID);
//                if(driver != null) {
//                    LinkedList<LaptimeStorage> list = driver.getLaptimes().getLaptimes();
//                    if (list.size() > 0) {
//                        builder.append(RacingMessages.LAST_10_LAPS);
//                        list.forEach(laptimeOBJ -> builder.append(ChatColor.BOLD).append(ChatColor.GREEN).append(Utils.millisToTimeString(laptimeOBJ.getLaptime())).append(" | ").append(ChatColor.RESET).append(Utils.millisToTimeString(laptimeOBJ.getS1data().getSectorLength())).append("/").append(Utils.millisToTimeString(laptimeOBJ.getS2data().getSectorLength())).append("/").append(Utils.millisToTimeString(laptimeOBJ.getS3data().getSectorLength())).append("\n"));
//                        return builder.toString();
//                    } else {
//                        return RacingMessages.NO_LAPS_DRIVEN;
//                    }
//                } else {
                    return RacingMessages.NO_LAPS_DRIVEN;
//                }
            }
            default:
                return DefaultMessages.INVALID_TYPE;
        }
    }

    public String createRace(String raceName, String laps) {
        int intLaps;
        try {
            intLaps =Integer.parseInt(laps);
        } catch (NumberFormatException e) {
            return DefaultMessages.INVALID_NUMBER;
        }

        RACES.put(raceName, new Race(raceName, intLaps));
        return RacingMessages.RACE_CREATED;
    }

    public String listRaces() {
        StringBuilder builder = new StringBuilder();
        builder.append(RacingMessages.RACES_CREATED);
        RACES.forEach((s, race) -> builder.append(s).append(" : ").append(race.getLaps()).append("\n"));
        return builder.toString();
    }

    public Race getRace(String raceName) {
        return RACES.get(raceName);
    }

    private boolean raceExists(String raceName) {
        return RACES.containsKey(raceName);
    }

    public String updateRace(Player player, String raceName, String type, String input) {
        if(!raceExists(raceName)) return RacingMessages.RACE_DOES_NOT_EXIST;
        Race race = getRace(raceName);
        switch (type.toLowerCase()) {
            case "laps": {
                try {
                    int laps = Integer.parseInt(input);
                    race.setLaps(laps);
                    return DefaultMessages.PREFIX + "Laps have been changed.";
                } catch (NumberFormatException e) {
                    return DefaultMessages.INVALID_NUMBER;
                }
            }
            case "timetrial": {
                switch (input.toLowerCase()) {
                    case "disable":
                        race.setTimeTrialStatus(false);
                        return DefaultMessages.PREFIX + "Disabled timetrial on this track.";
                    case "enable":
                        race.setTimeTrialStatus(true);
                        return DefaultMessages.PREFIX + "Enabled timetrial on this track.";
                    case "setspawn":
                        race.getStorage().setTimeTrialSpawn(player.getLocation());
                        return DefaultMessages.PREFIX + "Spawn has been changed.";
                }
                break;
            }
            case "sector": {
                try {
                    Region region = Utils.getSession(player).getSelection(Utils.getAdaptedWorld(player.getWorld()));
                    switch (input.toLowerCase()) {
                        case "1":
                        case "sector1":
                        case "s1": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "s1");
                            race.getStorage().setS1(cuboid);
                            return DefaultMessages.PREFIX + "S1 has been changed.";
                        }
                        case "2":
                        case "sector2":
                        case "s2": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "s2");
                            race.getStorage().setS2(cuboid);
                            return DefaultMessages.PREFIX + "S2 has been changed.";
                        }
                        case "finish":
                        case "3":
                        case "sector3":
                        case "s3": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "s3");
                            race.getStorage().setS3(cuboid);
                            return DefaultMessages.PREFIX + "S3 has been changed.";
                        }
                        default: {
                            return DefaultMessages.PREFIX + "Not an valid sector";
                        }
                    }
                } catch (IncompleteRegionException e) {
                    return DefaultMessages.INVALID_SELECTION;
                }
            }
            case "pitlane": {
                try {
                    Region region = Utils.getSession(player).getSelection(Utils.getAdaptedWorld(player.getWorld()));
                    switch (input.toLowerCase()) {
                        case "pen":
                        case "pitentry": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "pitentry");
                            race.getStorage().setPitEntry(cuboid);
                            return DefaultMessages.PREFIX + "Pit Entry has been changed.";
                        }
                        case "pex":
                        case "pitexit": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "pitexit");
                            race.getStorage().setPitExit(cuboid);
                            return DefaultMessages.PREFIX + "Pit Exit has been changed.";
                        }
                        default: {
                            return DefaultMessages.PREFIX + "This is not an valid pitlane type.";
                        }
                    }
                } catch (IncompleteRegionException e) {
                    return DefaultMessages.INVALID_SELECTION;
                }
            }
            case "name": {
                race.setName(input.toLowerCase());
                return DefaultMessages.PREFIX + "Changed race name";
            }
            default:
                return DefaultMessages.PREFIX + "Invalid type: \n-Laps\n-Sector\n-Pitlane\n-Name";
        }
        return DefaultMessages.PREFIX + "Invalid type: \n-Laps\n-Sector\n-Pitlane\n-Name";
    }

    public String resetRace(String name) {
        RaceListener.reset(name);
        return DefaultMessages.PREFIX + "Race has been reset.";
    }

    public String toImage(String name) {
        return "Image created";
    }

}
