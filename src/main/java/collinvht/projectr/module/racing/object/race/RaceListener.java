package collinvht.projectr.module.racing.object.race;

import collinvht.projectr.module.main.listener.listeners.MainPlayerListener;
import collinvht.projectr.module.main.listener.listeners.MainVehicleListener;
import collinvht.projectr.module.main.objects.RaceDriver;
import collinvht.projectr.module.racing.manager.managers.RaceManager;
import collinvht.projectr.util.DefaultMessages;
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
                MainVehicleListener.getRACE_DRIVERS().forEach((uuid, raceDriver) ->  {
                    for (Race race : LISTENING) {
                        race.getRaceLapStorage().update(raceDriver);
                    }
                });
            }
        };
        if(timer == null) timer = new Timer("ProjectR RaceListener");
        timer.schedule(task, 0, 1);
    }

    public static String startListeningTo(Race race, int mode) {
        if(isListeningToRace(race)) return DefaultMessages.PREFIX + "That race already started.";
        RaceMode raceMode = RaceMode.getRace(mode);
        if(raceMode == null) return "That Race mode doesn't exist!";
        RaceCuboidStorage storage = race.getStorage();
        if(storage != null ) {
            if(!storage.allCuboidsSet()) return "Not the whole track is done setup, this is required to start";
            LISTENING.add(race);
            race.getRaceLapStorage().setRaceMode(raceMode);
        } else {
            return "Not the whole track is done setup, this is required to start";
        }

        return DefaultMessages.PREFIX + "Race started.";
    }

    public static String startListeningTo(Race race, int mode, UUID uuid) {
        return "";
    }
//        if(isListeningToAnRace()) {
//            return "Er is al een race bezig!";
//        } else {
//            RaceMode raceMode = RaceMode.getRace(mode);
//            if(raceMode == null) return "Die racemode bestaat niet!";
//
//            RaceCuboidStorage storage = race.getStorage();
//            if(storage != null ) {
//                if(!storage.allCuboidsSet()) return "Niet de hele baan is ingesteld, dit is vereist om te starten!";
//                currentRace = race;
//
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if(raceMode.equals(RaceMode.TIMETRIAL)) {
//                            if(uuid != null) {
//                                RaceDriver driver = MTListener.getRaceDrivers().get(uuid);
//                                if(driver != null) {
//                                    checkPlayer(driver, race, storage, raceMode);
//                                }
//                            }
//                        } else {
//                            MTListener.getRaceDrivers().forEach((uuid, raceDriver) -> {
//                                checkPlayer(raceDriver, race, storage, raceMode);
//                            });
//                        }
//                    }
//                };
//            if(timer == null) timer = new Timer("ProjectR RaceListener");
//            timer.scheduleAtFixedRate(task, 0, 1);
//            } else {
//                return "Niet de hele baan is ingesteld, dit is vereist om te starten!";
//            }
//
//            return "Race gestart!";
//        }
//    }

    private static void checkPlayer(RaceDriver raceDriver, Race race, RaceCuboidStorage storage, String mode) {
//        if(raceDriver.isDriving()) {
//            Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
//            if(player != null) {
//                if (player.isOnline()) {
//                    LaptimeStorage laptimeStorage = raceDriver.getLaptimes().getCurrentLap();
//                    if (raceDriver.getVehicle() != null) {
//                        String licensePlate = "";
//                        if (laptimeStorage == null) {
//                            laptimeStorage = new LaptimeStorage(player.getUniqueId(), race);
//                            raceDriver.getLaptimes().setCurrentLap(laptimeStorage);
//                        }
//                        Location location = player.getLocation();
//                        if (raceDriver.isPassedPitExit()) {
//                            if (!raceDriver.isInPit()) {
//                                if (storage.getPitEntry().getCuboid().containsLocation(location)) {
//                                    raceDriver.setInPit();
//                                }
//                            }
//                            if (storage.getS1().getCuboid().containsLocation(location)) {
//                                if (!laptimeStorage.isPassedS1()) {
//                                    laptimeStorage.setPassedS1(true);
//                                    laptimeStorage.setPassedS3(false);
//                                    laptimeStorage.setS1(System.currentTimeMillis());
//                                    if (!raceDriver.getLaptimes().isInvalidated() && !raceDriver.isDisqualified()) {
//                                        Messages.CIRCUIT_INFO_SECTOR.send(player, "1", laptimeStorage.getS1Color() + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorDifference(), "ss.SS"));
//                                    }
//                                    raceDriver.getLaptimes().addSector();
//                                    Bukkit.getLogger().warning(player.getDisplayName() + " s1");
//                                    return;
//                                }
//                            }
//                            if (storage.getS2().getCuboid().containsLocation(location)) {
//                                if (laptimeStorage.isPassedS1() && !laptimeStorage.isPassedS2()) {
//                                    laptimeStorage.setPassedS2(true);
//                                    laptimeStorage.setS2(System.currentTimeMillis());
//                                    if (!raceDriver.getLaptimes().isInvalidated() && !raceDriver.isDisqualified()) {
//                                        Messages.CIRCUIT_INFO_SECTOR.send(player, "2", laptimeStorage.getS2Color() + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorDifference(), "ss.SS"));
//                                    }
//                                    raceDriver.getLaptimes().addSector();
//                                    Bukkit.getLogger().warning(player.getDisplayName() + " s2");
//                                    return;
//                                }
//                            }
//                            if (storage.getS3().getCuboid().containsLocation(location)) {
//                                if (!laptimeStorage.isPassedS3()) {
//                                    if (laptimeStorage.isPassedS1() && laptimeStorage.isPassedS2()) {
//                                        laptimeStorage.setS3(System.currentTimeMillis());
//                                        if (!raceDriver.getLaptimes().isInvalidated() && !raceDriver.isDisqualified()) {
//                                            laptimeStorage.createLaptime();
//                                            LaptimeStorage clone = laptimeStorage.clone();
//                                            laptimeHash.put(raceDriver.getDriverUUID(), clone);
//                                            raceDriver.addLaptime(clone);
//                                            Messages.CIRCUIT_INFO_SECTOR.send(player, "3", laptimeStorage.getS3Color() + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorDifference(), "ss.SS"));
//                                            Messages.CIRCUIT_INFO_LAP_TIME.send(player, laptimeStorage.getLapColor(true) + Utils.millisToTimeString(laptimeStorage.getLaptime()) + " | " + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorDifference(), "ss.SS"));
//                                            Bukkit.getLogger().warning(player.getDisplayName() + " s3");
//                                        } else {
//                                            Messages.CIRCUIT_INFO_LAP_TIME_INVALID.send(player);
//                                            raceDriver.getLaptimes().setInvalidated(false);
//                                        }
//                                        raceDriver.getLaptimes().addSector();
//
//                                        laptimeStorage.setPassedS1(false);
//                                        laptimeStorage.setPassedS2(false);
//                                        laptimeStorage.setPassedS3(true);
//
//                                        raceDriver.setCurrentLap(raceDriver.getCurrentLap() + 1);
//                                        if (mode.isHasLaps()) {
//                                            if (raceDriver.getCurrentLap() >= race.getLaps()) {
//                                                raceDriver.setFinished(true);
//                                                final int position = finishers.size() + 1;
//                                                finishers.put(position, raceDriver.getDriverUUID());
//
//                                                Messages.CIRCUIT_INFO_FINISH_POS.send(player, String.valueOf(position));
//
//                                                for (Player p : Bukkit.getOnlinePlayers()) {
//                                                    if (Permissions.FIA_ADMIN.hasPermission(p) || Permissions.FIA_RACE.hasPermission(p)) {
//                                                        p.sendMessage(player.getDisplayName() + " is gefinished op plek " + position);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        drivingBackwards(raceDriver, player);
//                                    }
//                                }
//                            }
//                        } else {
//                            if (storage.getPitExit().getCuboid().containsLocation(location)) {
//                                raceDriver.setPassedPitExit();
//                                Bukkit.getLogger().warning(player.getDisplayName() + " pit out");
//                            }
//
////                            if((VehicleData.speed.get(licensePlate)*50) > 80.00D) {
////                                //player.sendMessage("Speeding in pits");
////                            }
//
//                        }
//                    }
//                } else {
//                    MTListener.getRaceDrivers().remove(raceDriver.getDriverUUID());
//                }
//            }
//        }
    }

    static void drivingBackwards(RaceDriver raceDriver, Player player) {
//        if(raceDriver.getLaptimes().isInvalidated()) return;
//        raceDriver.getLaptimes().setInvalidated(true);
//        Messages.CIRCUIT_INFO_BACKWARDS.send(player);
//        for (Player p : Bukkit.getOnlinePlayers()) {
//            if (Permissions.FIA_ADMIN.hasPermission(p) || Permissions.FIA_RACE.hasPermission(p)) {
//                p.sendMessage(player.getDisplayName() + " is most likely driving the circuit backwards.");
//            }
//        }
    }

    public static boolean isListeningToRace(Race race) {
        return LISTENING.contains(race);
    }

    public static String stopListening() {
        if(task == null) {
            return "Er is geen race bezig";
        }
        task.cancel();
        resetAll();
//        DiscordManager.resetMessage();

        return "Race gestopt.";
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

//        MTListener.getRaceDrivers().forEach((uuid, raceDriver) -> {
//            raceDriver.getLaptimes().resetLaptimes();
//            raceDriver.setDisqualified(false);
//        });
    }
    public static void reset(String raceName) {
        Race race = RaceManager.getInstance().getRace(raceName);
        if(isListeningToRace(race)) {
            race.getRaceLapStorage().reset();
        }
    }

    public static boolean isListeningToAnyRace() {
        return LISTENING.size() > 0;
    }
}
