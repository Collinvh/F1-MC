package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.util.DefaultMessages;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class RaceListener {
    @Getter
    private static final ArrayList<Race> LISTENING = new ArrayList<>();
    private static ScheduledTask task;

    public static void initialize() {
        task = F1MC.getAsyncScheduler().runAtFixedRate(F1MC.getInstance(), scheduledTask -> {
            ArrayList<Race> removingRaces = new ArrayList<>();
            for (Race race : LISTENING) {
                if (race.getRaceTimer() != null) {
                    race.getRaceTimer().update();
                    if (race.getRaceTimer().isFinished()) {
                        removingRaces.add(race);
                        F1MC.getLog().warning(RaceManager.getInstance().getRaceResult(race.getName(), "fastest", null));
                        break;
                    }
                }
            }
            if (!removingRaces.isEmpty()) {
                LISTENING.removeAll(removingRaces);
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
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
        if(task == null) {
            return;
        }
        task.cancel();
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
