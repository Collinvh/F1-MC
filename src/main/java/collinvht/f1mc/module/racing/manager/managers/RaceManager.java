package collinvht.f1mc.module.racing.manager.managers;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.discord.DiscordModule;
import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.module.main.objects.CountryObject;
import collinvht.f1mc.module.racing.module.team.manager.TeamManager;
import collinvht.f1mc.module.racing.module.team.object.TeamObj;
import collinvht.f1mc.module.racing.object.PenaltyCuboid;
import collinvht.f1mc.module.racing.object.race.RaceTimer;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceListener;
import collinvht.f1mc.module.racing.util.RacingMessages;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RaceManager extends ModuleBase {
    private static RaceManager instance;

    @Getter
    private static final HashMap<String, Race> RACES = new HashMap<>();
    @Getter
    private static final HashMap<Player, Race> drivingPlayers = new HashMap<>();

    @Getter
    private static boolean isRunningTimer;
    @Getter
    private static Race timingRace;

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
        if(timingRace != null) {
            if(timingRace.getRaceTimer() != null) {
                timingRace.getRaceTimer().stop();
            }
        }
    }

    private void saveRaces() {
        if(!RACES.isEmpty()) {
            RACES.forEach((s, race) -> race.saveJson());
        }
        RaceListener.stopListening(false);
    }

    private void loadRaces() {
        File raceFiles = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/races/").toFile();
        if(raceFiles.exists()) {
            File[] races = raceFiles.listFiles();
            if(races != null) {
                ArrayList<String> names = new ArrayList<>();
                for (File raceFile : races) {
                    try {
                        Race race = Race.createRaceFromJson((JsonObject) Utils.readJson(raceFile.getAbsolutePath()));
                        if(race != null) {
                            if(names.contains(race.getName())) return;
                            RACES.put(race.getName(), race);
                            race.updateLeaderboard();
                            names.add(race.getName());
                        }
                    } catch (Exception e) {
                        Bukkit.getLogger().severe(raceFile.getAbsolutePath() + " failed to load.");
                    }
                }
            }
        }
    }

    public String startRace(String raceName, String mode) {
        if(!raceExists(raceName)) return RacingMessages.RACE_DOES_NOT_EXIST;
        try {
            int modeInt = Integer.parseInt(mode);
            if(Utils.isEnableDiscordModule()) {
            DiscordModule module = DiscordModule.getInstance();
            if(module.isInitialized()) {
                TextChannel channel = module.getJda().getTextChannelById(1217628051853021194L);
                if(channel != null) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.addField("Race started at " + raceName, "Mode = " + mode, true);
                    builder.setColor(Color.BLUE);
                    channel.sendMessage(builder.build()).queue();
                }
            }
            }
            Race race = getRace(raceName);
            if(race.getRaceTimer() != null) {
                if (race.getRaceTimer().isFinished()) race.setRaceTimer(null);
            }
            return RaceListener.startListeningTo(race, modeInt);
        } catch (NumberFormatException e) {
            return DefaultMessages.INVALID_NUMBER;
        }
    }

    public String stopRace(String name) {
        Race race = RACES.get(name);
        if(RaceListener.isListeningToRace(race)) {
            if(Utils.isEnableDiscordModule()) {
                DiscordModule module = DiscordModule.getInstance();
                if (module.isInitialized()) {
                    TextChannel channel = module.getJda().getTextChannelById(1217628051853021194L);
                    if (channel != null) {
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.addField("Race result | " + name, race.getLaps() + " laps driven.", true);
                        builder.setColor(Color.GREEN);
                        HashMap<UUID, RaceDriver> drivers = VPListener.getRACE_DRIVERS();
                        if(!race.getRaceLapStorage().getRaceMode().isLapped()) {
                            if (drivers.values().toArray().length > 0) {
                                LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();
                                drivers.forEach((unused, driver) -> {
                                    if (driver.getLaptimes(race).getFastestLap() != null) {
                                        sectors.put(driver, driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength());
                                    }
                                });

                                ListOrderedMap<RaceDriver, Long> treeMap = Utils.sortByValueDesc(sectors);
                                if (treeMap.values().toArray().length > 0) {
                                    AtomicInteger pos = new AtomicInteger();
                                    treeMap.forEach((driver, aLong) -> {
                                        OfflinePlayer player = Bukkit.getOfflinePlayer(driver.getDriverUUID());
                                        pos.getAndIncrement();
                                        builder.addField(pos.get() + ".", player.getName() + " " + Utils.millisToTimeString(driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()), false);
                                    });
                                }
                            }
                        } else {
                            AtomicReference<RaceDriver> p1Finisher = new AtomicReference<>();
                            race.getRaceLapStorage().getFinishers().forEach((integer, uuid) -> {
                                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid.getDriverUUID());
                                CountryObject countryObject = CountryManager.getPlayerPerCountry().get(uuid.getDriverUUID());
                                TeamObj obj = TeamManager.getTeamForUUID(uuid.getDriverUUID());
                                if(integer > 1) {
                                    if(p1Finisher.get() != null) {
                                        builder.addField(integer + ". | " + player.getName() + Utils.millisToTimeString(p1Finisher.get().getFinishTime()-uuid.getFinishTime()), (obj == null ? "Team: ???" : "Team: " + obj.getTeamName()) + " Country: " + (countryObject == null ? "???" : countryObject.getCountryName()), false);
                                    } else {
                                        builder.addField(integer + ". | " + player.getName(), (obj == null ? "Team: ???" : "Team: " + obj.getTeamName()) + " Country: " + (countryObject == null ? "???" : countryObject.getCountryName()), false);
                                    }
                                } else {
                                    p1Finisher.set(uuid);
                                    builder.addField(integer + ". | " + player.getName(), (obj == null ? "Team: ???" : "Team: " + obj.getTeamName()) + " Country: " + (countryObject == null ? "???" : countryObject.getCountryName()) , false);
                                }
                            });
                        }
                        channel.sendMessage(builder.build()).queue();
                    }
                }
            }
            return RaceListener.stopListeningTo(race);
        } else {
            return DefaultMessages.PREFIX + "Race isn't running";
        }
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
                HashMap<UUID, RaceDriver> drivers = VPListener.getRACE_DRIVERS();
                if(drivers.values().toArray().length > 0) {
                    LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();
                    drivers.forEach((unused, driver) -> {
                        if (driver.getLaptimes(race).getFastestLap() != null) {
                            sectors.put(driver, driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength());
                        }
                    });

                    ListOrderedMap<RaceDriver, Long> treeMap = Utils.sortByValueDesc(sectors);
                    if (treeMap.values().toArray().length > 0) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(RacingMessages.FASTEST_LAPS);
                        AtomicInteger pos = new AtomicInteger();
                        treeMap.forEach((driver, aLong) -> {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(driver.getDriverUUID());
                            pos.getAndIncrement();
                            builder.append(pos.get()).append(". ").append(player.getName()).append(" ").append(Utils.millisToTimeString(driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength())).append("\n");
                        });
                        return builder.toString();
                    }
                }
                return RacingMessages.NO_LAPS_DRIVEN;
            }
            case "result": {
                if(!race.getRaceLapStorage().getFinishers().isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(RacingMessages.RACE_RESULT);
                    race.getRaceLapStorage().getFinishers().forEach((integer, uuid) -> {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid.getDriverUUID());
                        builder.append(integer).append(". | ").append(player.getName()).append("\n");
                    });
                    return builder.toString();
                } else {
                    return RacingMessages.NO_FINISHERS;
                }
            }
            case "position": {
                StringBuilder builder = new StringBuilder();
                HashMap<UUID, RaceDriver> drivers = VPListener.getRACE_DRIVERS();
                if(!drivers.isEmpty()) {
                    builder.append(RacingMessages.RACE_POSITION);

                    LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();

                    drivers.forEach((unused, driver) -> sectors.put(driver, (long) driver.getLaptimes(race).getSectors()));

                    ListOrderedMap<RaceDriver, Long> treeMap = Utils.sortByValueDesc(sectors);


                    if (!treeMap.isEmpty()) {
                        builder.append(RacingMessages.SECTOR_POSITION);

                        AtomicInteger pos = new AtomicInteger();
                        treeMap.forEach((driver, integer) -> {
                            if (integer > 0) {
                                OfflinePlayer player = Bukkit.getOfflinePlayer(driver.getDriverUUID());
                                builder.append(pos.incrementAndGet()).append(". ").append(player.getName()).append(" : ").append(integer).append("\n");
                            }
                        });
                        return builder.toString();
                    }
                } else {
                    return RacingMessages.NO_LAPS_DRIVEN;
                }
            }
            case "record": {
                StringBuilder builder = new StringBuilder();
                RaceDriver driver = VPListener.getRACE_DRIVERS().get(playerUUID);
                if (driver != null) {
                    LinkedList<LaptimeStorage> list = driver.getLaptimes(race).getLaptimes();
                    if (!list.isEmpty()) {
                        builder.append(RacingMessages.LAST_10_LAPS);
                        list.forEach(laptimeOBJ -> builder.append(ChatColor.BOLD).append(ChatColor.GREEN).append(Utils.millisToTimeString(laptimeOBJ.getLapData().getSectorLength())).append(" | ").append(ChatColor.RESET).append(Utils.millisToTimeString(laptimeOBJ.getS1().getSectorLength())).append("/").append(Utils.millisToTimeString(laptimeOBJ.getS2().getSectorLength())).append("/").append(Utils.millisToTimeString(laptimeOBJ.getS3().getSectorLength())).append("\n"));
                        return builder.toString();
                    } else {
                        return RacingMessages.NO_LAPS_DRIVEN;
                    }
                } else {
                    return RacingMessages.NO_LAPS_DRIVEN;
                }
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

    public String updateRace(Player player, String raceName, String type, String input, String[] extraInput) {
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
                    case "spawn":
                    case "setspawn":
                        race.getStorage().setTimeTrialSpawn(player.getLocation());
                        return DefaultMessages.PREFIX + "Spawn has been changed.";
                    case "head":
                    case "skull":
                        if(extraInput[4] != null) {
                            int number = 0;
                            try {
                                number = Integer.parseInt(extraInput[4]);
                            } catch (NumberFormatException e) {
                                return DefaultMessages.PREFIX + "Invalid number";
                            }
                            race.getStorage().setSkullId(number);
                            return DefaultMessages.PREFIX + "Spawn has been changed.";
                        } else return DefaultMessages.PREFIX + "Provide an number";
                    case "leader":
                    case "leaderboard":
                    case "setleaderboard":
                    case "setleader":
                        race.getStorage().setTimeTrialLeaderboard(player.getLocation());
                        race.updateLeaderboard();
                        return DefaultMessages.PREFIX + "Leaderboard has been changed.";
                }
                break;
            }
            case "offtrack": {
                try {
                    Region region = Utils.getSession(player).getSelection(Utils.getAdaptedWorld(player.getWorld()));
                    String name = input.toLowerCase();
                    switch (name) {
                        case "delete": {
                            if(extraInput[4] != null) {
                                if (race.getStorage().getLimits().containsKey(extraInput[4].toLowerCase())) {
                                    race.getStorage().getLimits().remove(extraInput[4].toLowerCase());
                                    return DefaultMessages.PREFIX + "Removed track limit.";
                                } else {
                                    return DefaultMessages.PREFIX + "That name doesn't exist";
                                }
                            } else {
                                return DefaultMessages.PREFIX + "You didn't provide a name";
                            }
                        }
                        case "list": {
                            StringBuilder str = new StringBuilder(DefaultMessages.PREFIX + "Tracklimits:\n");
                            for (NamedCuboid limit : race.getStorage().getLimits().values()) {
                                str.append(limit.getName()).append("\n");
                            }
                            return str.toString();
                        }
                        default: {
                            int flags = 0;
                            if(extraInput[4] != null) {
                                try {
                                    flags = Integer.parseInt(extraInput[4]);
                                } catch (NumberFormatException e) {
                                    return DefaultMessages.PREFIX + "Invalid Number";
                                }
                            }
                            PenaltyCuboid cuboid = race.getStorage().createPenaltyCuboidFromSelection(player.getWorld(), region, name, flags);
                            race.getStorage().getLimits().put(name, cuboid);
                            return DefaultMessages.PREFIX + "Added tracklimit";
                        }
                    }
                } catch (IncompleteRegionException e) {
                    return DefaultMessages.INVALID_SELECTION;
                }
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
                        case "mini_1":
                            if(extraInput[4] != null) {
                                if(extraInput[4].equalsIgnoreCase("delete")) {
                                    if(extraInput[5] != null) {
                                        race.getStorage().getS1_mini().remove(extraInput[5]);
                                        return DefaultMessages.PREFIX + "Mini in s1 has been removed";
                                    } else {
                                        return DefaultMessages.PREFIX + "Invalid arguments";
                                    }
                                } else if (extraInput[4].equalsIgnoreCase("list")) {
                                    StringBuilder str = new StringBuilder(DefaultMessages.PREFIX + "Minis:\n");
                                    for (NamedCuboid limit : race.getStorage().getS1_mini().values()) {
                                        str.append(limit.getName()).append("\n");
                                    }
                                    return str.toString();
                                }
                                NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, extraInput[4]);
                                race.getStorage().getS1_mini().put(extraInput[4], cuboid);
                                return DefaultMessages.PREFIX + "Mini in s1 has been added.";
                            } else {
                                return DefaultMessages.PREFIX + "Invalid arguments";
                            }
                        case "mini_2":
                            if(extraInput[4] != null) {
                                if(extraInput[4].equalsIgnoreCase("delete")) {
                                    if(extraInput[5] != null) {
                                        race.getStorage().getS2_mini().remove(extraInput[5]);
                                        return DefaultMessages.PREFIX + "Mini in s2 has been removed";
                                    } else {
                                        return DefaultMessages.PREFIX + "Invalid arguments";
                                    }
                                } else if (extraInput[4].equalsIgnoreCase("list")) {
                                    StringBuilder str = new StringBuilder(DefaultMessages.PREFIX + "Minis:\n");
                                    for (NamedCuboid limit : race.getStorage().getS2_mini().values()) {
                                        str.append(limit.getName()).append("\n");
                                    }
                                    return str.toString();
                                }
                                NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, extraInput[4]);
                                race.getStorage().getS2_mini().put(extraInput[4], cuboid);
                                return DefaultMessages.PREFIX + "Mini in s2 has been added.";
                            } else {
                                return DefaultMessages.PREFIX + "Invalid arguments";
                            }
                        case "mini_3":
                            if(extraInput[4] != null) {
                                if(extraInput[4].equalsIgnoreCase("delete")) {
                                    if(extraInput[5] != null) {
                                        race.getStorage().getS3_mini().remove(extraInput[5]);
                                        return DefaultMessages.PREFIX + "Mini in s3 has been removed";
                                    } else {
                                        return DefaultMessages.PREFIX + "Invalid arguments";
                                    }
                                } else if (extraInput[4].equalsIgnoreCase("list")) {
                                    StringBuilder str = new StringBuilder(DefaultMessages.PREFIX + "Minis:\n");
                                    for (NamedCuboid limit : race.getStorage().getS3_mini().values()) {
                                        str.append(limit.getName()).append("\n");
                                    }
                                    return str.toString();
                                }
                                NamedCuboid cuboid = race.getStorage().createNamedCuboidFromSelection(player.getWorld(), region, extraInput[4]);
                                race.getStorage().getS3_mini().put(extraInput[4], cuboid);
                                return DefaultMessages.PREFIX + "Mini in s3 has been added.";
                            } else {
                                return DefaultMessages.PREFIX + "Invalid arguments";
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
        return DefaultMessages.PREFIX + RaceListener.reset(name);
    }

    public String toImage(String raceName) {
        if(!raceExists(raceName)) return RacingMessages.RACE_DOES_NOT_EXIST;
        Race race = getRace(raceName);
        try {
            final BufferedImage image = ImageIO.read(new URL(
                    "https://media.discordapp.net/attachments/634115064567431189/1069386920590852106/image.png?ex=6600ac2e&is=65ee372e&hm=ae1d99317b169e8976bb3f4e6f0839e98c78c88087783d361507529d1a9f8ab3"));
            Graphics imageGraphics = image.getGraphics();
            Font basefont = new Font("Bahnschrift", Font.BOLD, 16);
            HashMap<UUID, RaceDriver> drivers = VPListener.getRACE_DRIVERS();
            if(drivers.values().toArray().length > 0) {
                LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();
                if(!race.getRaceLapStorage().getRaceMode().isLapped()) {
                    drivers.forEach((unused, driver) -> {
                        if (driver.getLaptimes(race).getFastestLap() != null) {
                            sectors.put(driver, driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength());
                        }
                    });
                } else {
                    race.getRaceLapStorage().getFinishers().forEach((integer, uuid) -> {
                        RaceDriver driver = VPListener.getRACE_DRIVERS().get(uuid.getDriverUUID());
                        if (driver.getLaptimes(race).getFastestLap() != null) {
                            sectors.put(driver, integer.longValue());
                        }
                    });
                }
                AtomicInteger pos = new AtomicInteger();
                ListOrderedMap<RaceDriver, Long> treeMap = Utils.sortByValueDesc(sectors);
                if (treeMap.values().toArray().length > 0) {
                    treeMap.forEach((driver, unused) -> {
                        pos.getAndIncrement();
                        if(pos.get() <21) {
                            String team = "N/A";
                            Player player = Bukkit.getPlayer(driver.getDriverUUID());
                            if(player == null) return;

                            if(pos.get() == 1) {
                                try {
                                    final BufferedImage character = ImageIO.read(new URL(
                                            "https://crafatar.com/renders/body/" + driver.getDriverUUID()       ));
                                    imageGraphics.drawImage(character, 132, 70, null);
                                    Font font = new Font("Calibri", Font.PLAIN, 30);
                                    imageGraphics.setColor(Color.WHITE);
                                    imageGraphics.setFont(font);
                                    imageGraphics.drawString(player.getName(), 32, 365);

                                    font = new Font("Calibri", Font.PLAIN, 15);
                                    imageGraphics.setFont(font);
                                    imageGraphics.drawString(team, 32, 390);
                                } catch (IOException ignored) {
                                }
                            }

                            imageGraphics.setFont(basefont);
                            imageGraphics.drawString(player.getName(), 615, 95 + (95 * (pos.get() - 1)));
                            imageGraphics.drawString(team, 760, 95 + (23 * (pos.get() - 1)));
                            imageGraphics.drawString(Utils.millisToTimeString(driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()), 895, 95 + (23 * (pos.get() - 1)));
                        }
                    });

                    imageGraphics.dispose();
                    File path = new File(F1MC.getInstance().getDataFolder() + "/temp/");
                    File file = new File(path + "/result.png");
                    if(!path.mkdirs()) {
                        Files.createDirectories(Paths.get(path.toURI()));
                    }

                    ImageIO.write(image, "png", file);

                    DiscordModule module = DiscordModule.getInstance();
                    if(module.isInitialized()) {
                        TextChannel channel = module.getJda().getTextChannelById(1217628051853021194L);
                        if(channel != null) {
                            channel.sendFile(file).queue();
                        }
                    }
                    file.delete();
                }
            } else {
                return "Can't create image with no race result";
            }
            return "Image created";
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.getMessage());
            return "Error creating image.";
        }
    }
    public String stopTimer() {
        if(!isRunningTimer) return "No timer running at the moment";
        timingRace.getRaceTimer().stop();
        return "Timer Stopped";
    }

    public static void setIsRunningTimer(boolean bool) {
        if(!bool) {
            timingRace = null;
        }
        isRunningTimer = bool;
    }

    public String createTimer(String race, String arg) {
        if(isRunningTimer) return "There is already a timer running!";
        if(!raceExists(race)) return RacingMessages.RACE_DOES_NOT_EXIST;
        Race raceObj = getRace(race);
        if(RaceListener.isListeningToRace(raceObj)) {
            if(raceObj.getRaceLapStorage().getRaceMode().isLapped()) {
                return "Can't start a timer for a lapped mode.";
            }
        }
        try {
            double length = Double.parseDouble(arg);
            raceObj.setRaceTimer(new RaceTimer((long) ((length*1000)*60)));
            timingRace = raceObj;
            isRunningTimer = true;
            return "Timer Started";
        } catch (NumberFormatException e) {
            return DefaultMessages.INVALID_NUMBER;
        }
    }

    public Race getRaceForPlayer(Player player) {
        return getDrivingPlayers().get(player);
    }
}
