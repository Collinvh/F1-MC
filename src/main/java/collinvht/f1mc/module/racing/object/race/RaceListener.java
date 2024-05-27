package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;


public class RaceListener {
    @Getter
    private static final ArrayList<Race> LISTENING = new ArrayList<>();
    private static Timer timer;

    public static void initialize() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ArrayList<Race> removingRaces = new ArrayList<>();
                for (Race race : LISTENING) {
                    if (race.getRaceTimer() != null) {
                        race.getRaceTimer().update();
                        if (race.getRaceTimer().isFinished()) {
                            removingRaces.add(race);
                            Bukkit.getLogger().warning(RaceManager.getInstance().getRaceResult(race.getName(), "fastest", null));
                            break;
                        }
                    }
                }
                if (!removingRaces.isEmpty()) {
                    LISTENING.removeAll(removingRaces);
                }
            }
        };
        if(timer == null) timer = new Timer("F1MC RaceListener");
        timer.schedule(task, 0, 1);
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

        return DefaultMessages.PREFIX + "Race started.";
    }

    public static boolean isListeningToRace(Race race) {
        return LISTENING.contains(race);
    }

    public static void stopListening(boolean b) {
        if(timer == null) {
            return;
        }
        timer.cancel();
        if(b) resetAll();
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
}
