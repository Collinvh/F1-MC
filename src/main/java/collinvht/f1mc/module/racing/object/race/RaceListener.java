package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.discord.DiscordModule;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Permissions;
import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;


public class RaceListener {
    private static final ArrayList<Race> LISTENING = new ArrayList<>();
    private static TimerTask task;
    private static Timer timer;

    public static void initialize() {
        task = new TimerTask() {
            @Override
            public void run() {
                VPListener.getRACE_DRIVERS().forEach((uuid, raceDriver) ->  {
                    for (Race race : LISTENING) {
                        race.getRaceLapStorage().update(raceDriver);
                    }
                });
            }
        };
        if(timer == null) timer = new Timer("F1MC RaceListener");
        timer.scheduleAtFixedRate(task, 0, 1);
    }

    public static String startListeningTo(Race race, int mode) {
        if(isListeningToRace(race)) return DefaultMessages.PREFIX + "That race already started.";
        RaceMode raceMode = RaceMode.getRace(mode);
        if(raceMode == null) return "That race mode doesn't exist!";
        RaceCuboidStorage storage = race.getStorage();
        if(storage != null ) {
            if(!storage.allCuboidsSet()) return "Not the whole track has been setup, this is required to start";
            LISTENING.add(race);
            race.getRaceLapStorage().setRaceMode(raceMode);
        } else {
            return "Not the whole track has been setup, this is required to start";
        }

        DiscordModule discordModule = DiscordModule.getInstance();
        if(discordModule.isInitialized()) {
            JDA jda = discordModule.getJda();
        }

        return DefaultMessages.PREFIX + "Race started.";
    }

    static void drivingBackwards(RaceDriver raceDriver, Race race, Player player) {
        if(raceDriver.getLaptimes(race).isInvalidated()) return;
        raceDriver.getLaptimes(race).setInvalidated(true);
        player.sendMessage(DefaultMessages.PREFIX + ChatColor.RED + " Your time got invalidated because your driving backwards!");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Permissions.FIA_ADMIN.hasPermission(p) || Permissions.FIA_RACE.hasPermission(p)) {
                p.sendMessage(player.getDisplayName() + " is most likely driving the circuit backwards.");
            }
        }
    }

    public static boolean isListeningToRace(Race race) {
        return LISTENING.contains(race);
    }

    public static void stopListening() {
        if(task == null) {
            return;
        }
        task.cancel();
        resetAll();
//        DiscordManager.resetMessage();
    }

    public static String stopListeningTo(Race race) {
        LISTENING.remove(race);
        return DefaultMessages.PREFIX + "Stopped Race.";
    }

    public static void resetAll() {
        for (Race race : LISTENING) {
            race.getRaceLapStorage().reset();
        }
        LISTENING.clear();

        VPListener.getRACE_DRIVERS().forEach((uuid, raceDriver) -> {
            raceDriver.getLaptimes().forEach((race, driverLaptimeStorage) -> driverLaptimeStorage.resetLaptimes());
            raceDriver.setDisqualified(false);
        });
    }
    public static String reset(String raceName) {
        Race race = RaceManager.getInstance().getRace(raceName);
        if(race == null) return "Race doesn't exist";
        if(isListeningToRace(race)) {
            race.getRaceLapStorage().reset();
        }
        return "Race has been reset.";
    }

    public static boolean isListeningToAnyRace() {
        return !LISTENING.isEmpty();
    }
}
