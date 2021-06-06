package collinvht.zenticracing.listener.driver;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.racing.RaceManager;
import collinvht.zenticracing.commands.racing.laptime.object.Laptime;
import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.TeamBaan;
import collinvht.zenticracing.commands.team.object.TeamBaanObject;
import collinvht.zenticracing.commands.team.object.TeamObject;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DriverManager {

    @Getter
    private static final HashMap<UUID, DriverObject> drivers = new HashMap<>();
    @Getter
    private static final HashMap<UUID, Integer> taskIds = new HashMap<>();



    public static void addDriver(DriverObject object) {
        drivers.put(object.getPlayerUUID(), object);
    }

    public static void removeDriver(UUID uuid) {
        drivers.remove(uuid);
    }

    public static DriverObject getDriver(UUID uuid) {
        return drivers.get(uuid);
    }


    public static void createScoreboard(DriverObject driver) {
        if(taskIds.get(driver.getPlayerUUID()) != null) {
            int id = taskIds.get(driver.getPlayerUUID());
            Bukkit.getScheduler().cancelTask(id);
        }

        taskIds.put(driver.getPlayerUUID(), new BukkitRunnable() {
            private final ScoreboardManager manager = Bukkit.getScoreboardManager();

            @Override
            public void run() {
                TeamObject teamObject = Team.checkTeamForPlayer(driver.getPlayer());

                TeamBaanObject object = null;
                if(teamObject != null) {
                    object = TeamBaan.getTeamBanen().get(teamObject.getTeamName().toLowerCase());
                }

                if (RaceManager.getRunningRace() != null || object != null) {
                    if(object != null && RaceManager.getRunningRace() == null) {
                        if (!object.getObject().isRunning()) {
                            return;
                        }
                    }
                    RaceObject raceObject = RaceManager.getRunningRace();
                    if(raceObject == null && object != null) {
                        raceObject = object.getObject();
                    }

                    if(raceObject == null) {
                        return;
                    }

                    if (driver.isDriving()) {
                        Scoreboard scoreboard = manager.getNewScoreboard();

                        Objective o = scoreboard.registerNewObjective("zenticracing", "raceBoard");

                        o.setDisplayName("" + ChatColor.BLUE + ChatColor.BOLD + "Zentic" + ChatColor.WHITE + ChatColor.BOLD + "Racing");
                        o.setDisplaySlot(DisplaySlot.SIDEBAR);


                        HashMap<UUID, DriverObject> drivers = DriverManager.getDrivers();
                        LinkedHashMap<DriverObject, Integer> sectors = new LinkedHashMap<>();

                        drivers.forEach((String, driver) -> {
                            if(driver.getLapstorage().getSectors() > 0) {
                                sectors.put(driver, driver.getLapstorage().getSectors());
                            }
                        });

                        LinkedHashMap<DriverObject, Integer> treeMap = sortByValueDesc(sectors);

                        if(raceObject.getRunningMode() != null) {
                            if(raceObject.getRunningMode().isHasLaps()) {
                                Score score = o.getScore(ChatColor.GRAY + "Lap : " + driver.getRaceStorage().getLap() + " / " +  raceObject.getLapCount());
                                score.setScore(15);
                            }
                        }

                        AtomicInteger number = new AtomicInteger(1);
                        treeMap.forEach((driver, integer) -> {
                            if (number.get() > 6) {
                                return;
                            }

                            Score score = o.getScore(ChatColor.GRAY + "Nr " + number + ". " + driver.getPlayer().getName() + " : " + integer);
                            score.setScore(14 - number.get());
                            number.getAndIncrement();
                        });

                        Score score = o.getScore(" ");
                        score.setScore(6);

                        String fastestTime = "";
                        try {
                            fastestTime = Laptime.millisToTimeString(raceObject.getListener().getBestLapTime().getLapData().getSectorLength());
                        } catch (Exception ignored) {
                        }

                        Score fastest = o.getScore("Fastest : " + fastestTime);
                        fastest.setScore(5);

                        String personalTime = "";
                        if (driver.getLapstorage().getBestTime() != null) {
                            personalTime = Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getLaptime());
                        }

                        Score personal = o.getScore("PB : " + personalTime);
                        personal.setScore(4);

                        Score score2 = o.getScore("");
                        score2.setScore(3);

                        Score yourcheckpoints = o.getScore("Checkpoints : " + driver.getLapstorage().getSectors());
                        yourcheckpoints.setScore(2);

                        Score bestLap = o.getScore(getLapInfo(driver.getLapstorage().getCurrentLap()));
                        bestLap.setScore(1);

                        driver.getPlayer().setScoreboard(scoreboard);
                    }
                } else {
                    driver.getPlayer().setScoreboard(manager.getNewScoreboard());
                }
            }
        }.runTaskTimer(ZenticRacing.getRacing(), 0, 20).getTaskId());
    }

    public static String getLapInfo(Laptime laptimeOBJ) {
        if(laptimeOBJ != null) {
            String s1 = millisToTimeString(laptimeOBJ.getS1());
            String s2 = millisToTimeString(laptimeOBJ.getS2());
            String s3 = millisToTimeString(laptimeOBJ.getS3());

            return laptimeOBJ.getS1data().getSectorColor() + s1 + " | " + laptimeOBJ.getS2data().getSectorColor() + s2 + " | " + laptimeOBJ.getS3data().getSectorColor() + s3 + " |";
        }
        return "";
    }

    public static String millisToTimeString(final long mSec) {
        final String pattern = "ss.SSS";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(mSec));
    }

    public static LinkedHashMap<DriverObject, Integer> sortByValueDesc(Map<DriverObject, Integer> map) {
        List<Map.Entry<DriverObject, Integer>> list = new LinkedList(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        LinkedHashMap<DriverObject, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<DriverObject, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
