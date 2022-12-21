package collinvht.projectr.util.objects.race;

import collinvht.projectr.ProjectR;
import collinvht.projectr.listener.MTListener;
import collinvht.projectr.util.Utils;
import collinvht.projectr.util.objects.race.laptime.LaptimeStorage;
import collinvht.projectr.util.objects.race.laptime.Laptimes;
import lombok.Getter;
import lombok.Setter;
import nl.mtvehicles.core.infrastructure.helpers.VehicleData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class RaceListener {
    @Getter
    private static RaceListener instance;

    @Getter
    private final HashMap<UUID, LaptimeStorage> laptimeHash = new HashMap<>();

    @Getter @Setter
    private LaptimeStorage bestLapTime;

    @Getter @Setter
    private long bestS1 = -1;

    @Getter @Setter
    private long bestS2 = -1;

    @Getter @Setter
    private long bestS3 = -1;

    private Race currentRace;
    private int runnableID = -1;

    public static void initialize() {
        instance = new RaceListener();
    }

    public String startListeningTo(Race race, int mode) {
        if(isListeningToAnRace()) {
            return "Er is al een race bezig!";
        } else {
            RaceMode raceMode = RaceMode.getRace(mode);
            if(raceMode == null) return "Die racemode bestaat niet!";

            RaceStorage storage = race.getStorage();
            if(storage != null ) {
                if(!storage.allCuboidsSet()) return "Niet de hele baan is ingesteld, dit is vereist om te starten!";
                currentRace = race;
                runnableID = new BukkitRunnable() {
                    @Override
                    public void run() {
                        MTListener.getRaceDrivers().forEach((uuid, raceDriver) -> {
                            if(raceDriver.isDriving()) {
                                Player player = Bukkit.getPlayer(uuid);
                                if(player != null) {
                                    if (player.isOnline()) {
                                        LaptimeStorage laptimeStorage = raceDriver.getLaptimes().getCurrentLap();
                                        if (raceDriver.getVehicle() != null) {
                                            String licensePlate = raceDriver.getVehicle().getLicensePlate();
                                            if (laptimeStorage == null) {
                                                laptimeStorage = new LaptimeStorage(player.getUniqueId(), race);
                                                raceDriver.getLaptimes().setCurrentLap(laptimeStorage);
                                            }
                                            if (raceDriver.isPassedPitExit()) {
                                                if (!raceDriver.isInPit()) {
                                                    if (storage.getPitEntry().getCuboid().containsLocation(player.getLocation())) {
                                                        raceDriver.setInPit();
                                                        player.sendMessage("Currently in pit");
                                                    }
                                                }
                                                if (!laptimeStorage.isPassedS1()) {
                                                    if (storage.getS1().getCuboid().containsLocation(player.getLocation())) {
                                                        laptimeStorage.setPassedS1(true);
                                                        laptimeStorage.setPassedS3(false);
                                                        if (!raceDriver.getLaptimes().isInvalidated()) {
                                                            laptimeStorage.setS1(System.currentTimeMillis());

                                                            player.sendMessage(ChatColor.GRAY + " Je tijd in sector 1 was " + laptimeStorage.getS1Color() + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorLength()) + "\n");
                                                        }
                                                        raceDriver.getLaptimes().addSector();
                                                    }
                                                }
                                                if (laptimeStorage.isPassedS1() && !laptimeStorage.isPassedS2()) {
                                                    if (storage.getS2().getCuboid().containsLocation(player.getLocation())) {
                                                        laptimeStorage.setPassedS2(true);
                                                        if (!raceDriver.getLaptimes().isInvalidated()) {
                                                            laptimeStorage.setS2(System.currentTimeMillis());

                                                            player.sendMessage(ChatColor.GRAY + " Je tijd in sector 2 was " + laptimeStorage.getS2Color() + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorLength()) + "\n");
                                                        }
                                                        raceDriver.getLaptimes().addSector();
                                                    }
                                                }
                                                if (storage.getS3().getCuboid().containsLocation(player.getLocation())) {
                                                    if (!laptimeStorage.isPassedS3()) {
                                                        if (laptimeStorage.isPassedS1() && laptimeStorage.isPassedS2()) {
                                                            laptimeStorage.getS1data().setSectorStart(System.currentTimeMillis());
                                                            if (!raceDriver.getLaptimes().isInvalidated()) {
                                                                laptimeStorage.setS3(System.currentTimeMillis());
                                                                player.sendMessage(ChatColor.GRAY + " Je tijd in sector 3 was " + laptimeStorage.getS3Color() + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorLength()) + "\n");

//                                                                if (vehicle.getBaseVehicle().getName().toLowerCase().contains("f1")) {
//                                                                    TeamObject teamObject = Team.checkTeamForPlayer(player);
//                                                                    if (teamObject != null) {
//                                                                        RaceCar car = teamObject.getRaceCarFromVehicle(vehicle);
//                                                                        if (car != null) {
//                                                                            ItemStack stack = car.getBandGui().getItem(13);
//                                                                            if (stack != null) {
//                                                                                TyreData data = TyreManager.getDataFromTyre(stack);
//                                                                                laptime.setTyre(data.getTyre());
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                }

                                                                laptimeStorage.createLaptime();
                                                                LaptimeStorage clone = laptimeStorage.clone();
                                                                laptimeHash.put(uuid, clone);
                                                                raceDriver.addLaptime(clone);

                                                                player.sendMessage(ChatColor.GRAY + " Je laptijd was een " + laptimeStorage.getLapColor(true) + Utils.millisToTimeString(laptimeStorage.getLaptime()) + "\n");
                                                            } else {
                                                                player.sendMessage(ChatColor.RED + " Je laptijd was INVALIDATED.");
                                                                raceDriver.getLaptimes().setInvalidated(false);
                                                            }
                                                            raceDriver.getLaptimes().addSector();

                                                            laptimeStorage.setPassedS1(false);
                                                            laptimeStorage.setPassedS2(false);

//                                                            driver.getRaceStorage().setLap(driver.getRaceStorage().getLap() + 1);
//                                                            if (object.getRunningMode().isHasLaps()) {
//                                                                if (driver.getRaceStorage().getLap() >= object.getLapCount()) {
//                                                                    driver.getRaceStorage().setFinished(true);
//                                                                    FinishData data = new FinishData(driver, object.getFinishedDrivers().size() + 1);
//                                                                    object.addFinishedDriver(data);
//
//                                                                    player.sendMessage(ChatColor.GRAY + "Je bent gefinished op plek " + data.getFinishPosition());
//
//                                                                    for (Player p : Bukkit.getOnlinePlayers()) {
//                                                                        if (p.hasPermission("projectr.fia.race") || p.hasPermission("projectr.admin")) {
//                                                                            p.sendMessage(player.getDisplayName() + " is gefinished op plek " + data.getFinishPosition());
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (storage.getPitExit().getCuboid().containsLocation(player.getLocation())) {
                                                    raceDriver.setPassedPitExit();
                                                    player.sendMessage("Pit out");
                                                }

                                                if((VehicleData.speed.get(licensePlate)*50) > 80.00D) {
                                                    player.sendMessage("Speeding in pits");
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }.runTaskTimer(ProjectR.getInstance(), 0, 1).getTaskId();
            } else {
                return "Niet de hele baan is ingesteld, dit is vereist om te starten!";
            }

            return "Race gestart!";
        }
    }

    public boolean isListeningToAnRace() {
        return currentRace != null;
    }

    public String stopListening() {
        if(runnableID == -1) {
            return "Er is geen race bezig";
        }
        Bukkit.getScheduler().cancelTask(runnableID);
        runnableID = -1;
        currentRace = null;

        laptimeHash.clear();
        MTListener.getRaceDrivers().forEach((uuid, raceDriver) -> {
            raceDriver.getLaptimes().resetLaptimes();
        });
        LaptimeStorage bestLapTime = null;
        long bestS1 = -1;
        long bestS2 = -1;
        long bestS3 = -1;


        return "Race gestopt.";
    }
}
