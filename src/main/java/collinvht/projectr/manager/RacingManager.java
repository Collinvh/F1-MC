package collinvht.projectr.manager;
import collinvht.projectr.ProjectR;
import collinvht.projectr.util.JSONUtil;
import collinvht.projectr.util.WorldEditUtil;
import collinvht.projectr.util.objects.NamedCuboid;
import collinvht.projectr.util.objects.race.Race;
import collinvht.projectr.util.objects.race.RaceListener;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import scala.Int;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

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

    public String stopRace(String raceName) {
        if(!raceExists(raceName)) return "Race bestaat niet";
        return RaceListener.getInstance().stopListening();
    }

    public String deleteRace(String raceName) {
        if(raceExists(raceName)) {
            RACES.remove(raceName);
            return "Race is verwijderd.";
        } else {
            return "Die race bestaat niet";
        }
    }

    public String getRaceResult() {
        return null;
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

    private Race getRace(String raceName) {
        return RACES.get(raceName);
    }

    private boolean raceExists(String raceName) {
        return RACES.containsKey(raceName);
    }
}
