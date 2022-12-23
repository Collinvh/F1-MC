package collinvht.projectr.manager;
import collinvht.projectr.ProjectR;
import collinvht.projectr.listener.MTListener;
import collinvht.projectr.util.JSONUtil;
import collinvht.projectr.util.Utils;
import collinvht.projectr.util.WorldEditUtil;
import collinvht.projectr.util.objects.NamedCuboid;
import collinvht.projectr.util.objects.race.Race;
import collinvht.projectr.util.objects.race.RaceDriver;
import collinvht.projectr.util.objects.race.RaceListener;
import collinvht.projectr.util.objects.race.laptime.LaptimeStorage;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RacingManager {
    @Getter
    private static RacingManager instance;

    private static final HashMap<String, Race> RACES = new HashMap<>();

    private RacingManager() {
        loadRaces();
    }

    public static void initialize() {
        instance = new RacingManager();
        RaceListener.initialize();
    }

    public static void disable() {
        instance.saveRaces();
    }

    private void saveRaces() {
        if(!RACES.isEmpty()) {
            RACES.forEach((s, race) -> {
                race.saveJson();
            });
        }
    }

    private void loadRaces() {
        File raceFiles = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/races/").toFile();
        if(raceFiles.exists()) {
            File[] races = raceFiles.listFiles();
            if(races != null) {
                for (File raceFile : races) {
                    try {
                        Race race = Race.createRaceFromJson((JsonObject) JSONUtil.readJson(raceFile.getAbsolutePath()));
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
        if(!raceExists(raceName)) return "Race bestaat niet";
        try {
            int modeInt = Integer.parseInt(mode);
            return RaceListener.getInstance().startListeningTo(getRace(raceName), modeInt);
        } catch (NumberFormatException e) {
            return "Dat is geen geldig getal.";
        }
    }

    public String stopRace() {
        return RaceListener.getInstance().stopListening();
    }

    public String deleteRace(String raceName) {
        if(raceExists(raceName)) {
            if(RaceListener.getInstance().isListeningToAnRace()) {
                if(RaceListener.getInstance().getCurrentRace().getName().equals(raceName)) {
                    RaceListener.getInstance().stopListening();
                }
            }

            RACES.remove(raceName);
            return "Race is verwijderd.";
        } else {
            return "Die race bestaat niet";
        }
    }

    public String getRaceResult(String type, UUID playerUUID) {
        if(!RaceListener.getInstance().isListeningToAnRace()) return "Er is geen race bezig";
        switch (type) {
            case "timing":
            case "fastest": {
                HashMap<UUID, RaceDriver> drivers = MTListener.getRaceDrivers();
                if(drivers.values().toArray().length > 0) {
                    LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();
                    drivers.forEach((unused, driver) -> {
                        if (driver.getLaptimes().getFastestLap() != null) {
                            sectors.put(driver, driver.getLaptimes().getFastestLap().getLaptime());
                        }
                    });

                    LinkedHashMap<RaceDriver, Long> treeMap = Utils.sortByValueDesc(sectors);
                    if (treeMap.values().toArray().length > 0) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Snelste Laps:\n");
                        AtomicInteger pos = new AtomicInteger();
                        treeMap.forEach((driver, aLong) -> {
                            pos.getAndIncrement();
                            TextComponent component = new TextComponent();
                            OfflinePlayer player = Bukkit.getOfflinePlayer(driver.getDriverUUID());
                            component.setText(pos.get() + ". " + player.getName() + " " + Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getLapData().getSectorLength()));
                            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getS1data().getSectorLength()) + " | " + Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getS2data().getSectorLength()) + " | " + Utils.millisToTimeString(driver.getLaptimes().getFastestLap().getS3data().getSectorLength()))));
                            builder.append(component);
                        });
                        return builder.toString();
                    }
                }
                return "Er zijn nog geen laps gereden";
            }
            case "result": {
                if(RaceListener.getInstance().getFinishers().size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Race Result:\n");
                    RaceListener.getInstance().getFinishers().forEach((integer, uuid) -> {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                        builder.append(integer).append(". | ").append(player.getName());
                    });
                    return builder.toString();
                } else {
                    return "Er is nog niemand gefinished.";
                }
            }
            case "stand": {
                StringBuilder builder = new StringBuilder();
                HashMap<UUID, RaceDriver> drivers = MTListener.getRaceDrivers();
                if(drivers.size() > 0) {
                    builder.append("Race Stand:\n");

                    LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();

                    drivers.forEach((unused, driver) -> sectors.put(driver, (long) driver.getLaptimes().getSectors()));

                    LinkedHashMap<RaceDriver, Long> treeMap = Utils.sortByValueDesc(sectors);


                    if (treeMap.size() > 0) {
                        builder.append("Sector Stand\n");

                        AtomicInteger pos = new AtomicInteger();
                        treeMap.forEach((driver, integer) -> {
                            if (integer > 0) {
                                OfflinePlayer player = Bukkit.getOfflinePlayer(driver.getDriverUUID());
                                pos.getAndIncrement();
                                builder.append(pos.get()).append(". ").append(player.getName()).append(" : ").append(integer);
                            }
                        });
                        return builder.toString();
                    }
                } else {
                    return "Er hebben nog geen mensen gereden";
                }
            }
            case "record": {
                StringBuilder builder = new StringBuilder();
                RaceDriver driver = MTListener.getRaceDrivers().get(playerUUID);
                LinkedList<LaptimeStorage> list = driver.getLaptimes().getLaptimes();
                if(list.size() > 0) {
                    builder.append(" Jouw laatste tien laps :\n");
                    list.forEach(laptimeOBJ -> builder.append(ChatColor.BOLD).append(ChatColor.GREEN).append(Utils.millisToTimeString(laptimeOBJ.getLaptime())).append(" | ").append(ChatColor.RESET).append(Utils.millisToTimeString(laptimeOBJ.getS1data().getSectorLength())).append("/").append(Utils.millisToTimeString(laptimeOBJ.getS2data().getSectorLength())).append("/").append(Utils.millisToTimeString(laptimeOBJ.getS3data().getSectorLength())).append("\n"));
                    return builder.toString();
                } else {
                    return "Je hebt nog geen laps gereden";
                }
            }
            default:
                return "Dit is geen geldig type.";
        }
    }

    public String createRace(String raceName, String laps) {
        int intLaps;
        try {
            intLaps =Integer.parseInt(laps);
        } catch (NumberFormatException e) {
            return laps + " is geen geldig nummer.";
        }

        RACES.put(raceName, new Race(raceName, intLaps));
        return "Race is aangemaakt.";
    }

    public String listRaces() {
        StringBuilder builder = new StringBuilder();
        builder.append("Aangemaakte races; \n");
        RACES.forEach((s, race) -> builder.append(s).append(" : ").append(race.getLaps()).append("\n"));
        return builder.toString();
    }

    private Race getRace(String raceName) {
        return RACES.get(raceName);
    }

    private boolean raceExists(String raceName) {
        return RACES.containsKey(raceName);
    }

    public String updateRace(Player player, String raceName, String type, String input) {
        if(!raceExists(raceName)) return "Race bestaat niet";
        Race race = getRace(raceName);
        switch (type.toLowerCase()) {
            case "laps": {
                try {
                    int laps = Integer.parseInt(input);
                    race.setLaps(laps);
                    return "Laps zijn aangepast.";
                } catch (NumberFormatException e) {
                    return input + " is geen geldig nummer.";
                }
            }
            case "sector": {
                try {
                    Region region = WorldEditUtil.getSession(player).getSelection(WorldEditUtil.getAdaptedWorld(player.getWorld()));
                    switch (input.toLowerCase()) {
                        case "1":
                        case "sector1":
                        case "s1": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "s1");
                            race.getStorage().setS1(cuboid);
                            return "S1 is aangepast.";
                        }
                        case "2":
                        case "sector2":
                        case "s2": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "s2");
                            race.getStorage().setS2(cuboid);
                            return "S2 is aangepast.";
                        }
                        case "finish":
                        case "3":
                        case "sector3":
                        case "s3": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "s3");
                            race.getStorage().setS3(cuboid);
                            return "S3 is aangepast.";
                        }
                        default: {
                            return "Geen geldige sector";
                        }
                    }
                } catch (IncompleteRegionException e) {
                    return "Geen geldige worldedit selectie gevonden.";
                }
            }
            case "pitlane": {
                try {
                    Region region = WorldEditUtil.getSession(player).getSelection(WorldEditUtil.getAdaptedWorld(player.getWorld()));
                    switch (input.toLowerCase()) {
                        case "pen":
                        case "pitentry": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "pitentry");
                            race.getStorage().setPitEntry(cuboid);
                            return "Pit Entry is aangepast.";
                        }
                        case "pex":
                        case "pitexit": {
                            NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, "pitexit");
                            race.getStorage().setPitExit(cuboid);
                            return "Pit Exit is aangepast.";
                        }
                        default: {
                            return "Geen geldige pitlane sector";
                        }
                    }
                } catch (IncompleteRegionException e) {
                    return "Geen geldige worldedit selectie gevonden.";
                }
            }
            case "name": {
                race.setName(input.toLowerCase());
                return "Racenaam is aangepast.";
            }
            default:
                return "Ongeldig type; \n-Laps\n-Sector\n-Pitlane\n-Name";
        }
    }
}
