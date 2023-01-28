package collinvht.projectr.module.racing.object.race;

import collinvht.projectr.module.main.listener.listeners.MainVehicleListener;
import collinvht.projectr.module.main.objects.RaceDriver;
import collinvht.projectr.module.racing.object.laptime.LaptimeStorage;
import collinvht.projectr.util.Permissions;
import collinvht.projectr.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.UUID;

import static collinvht.projectr.module.racing.object.race.RaceListener.drivingBackwards;

public class RaceLapStorage {
    @Getter
    private final Race race;

    @Getter @Setter
    private RaceMode raceMode;

    @Getter
    private final LinkedHashMap<UUID, LaptimeStorage> laptimeHash = new LinkedHashMap<>();

    @Getter
    private final LinkedHashMap<Integer, UUID> finishers = new LinkedHashMap<>();

    @Getter @Setter
    private LaptimeStorage bestLapTime;

    @Getter @Setter
    private long bestS1 = -1;

    @Getter @Setter
    private long bestS2 = -1;

    @Getter @Setter
    private long bestS3 = -1;
    public RaceLapStorage(Race race) {
        this.race = race;
    }

    public void update(RaceDriver raceDriver) {
        if(raceDriver.isDriving()) {
            Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
            if(player != null) {
                if (player.isOnline()) {
                    RaceCuboidStorage storage = race.getStorage();
                    LaptimeStorage laptimeStorage = raceDriver.getLaptimes(race).getCurrentLap();
                    if (raceDriver.getVehicle() != null) {
                        if (laptimeStorage == null) {
                            laptimeStorage = new LaptimeStorage(player.getUniqueId(), race);
                            raceDriver.getLaptimes(race).setCurrentLap(laptimeStorage);
                        }
                        Location location = player.getLocation();
                        if (raceDriver.isPassedPitExit()) {
                            if (!raceDriver.isInPit()) {
                                if (storage.getPitEntry().getCuboid().containsLocation(location)) {
                                    raceDriver.setInPit();
                                }
                            }
                            if (storage.getS1().getCuboid().containsLocation(location)) {
                                if (!laptimeStorage.isPassedS1()) {
                                    laptimeStorage.setPassedS1(true);
                                    laptimeStorage.setPassedS3(false);
                                    laptimeStorage.setS1(System.currentTimeMillis());
                                    if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                                        player.sendMessage(ChatColor.GRAY + "Your time in S1 was " + laptimeStorage.getS1Color() + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorLength()) + "\n");
                                    }
                                    raceDriver.getLaptimes(race).addSector();
                                    Bukkit.getLogger().warning(player.getDisplayName() + " s1");
                                    return;
                                }
                            }
                            if (storage.getS2().getCuboid().containsLocation(location)) {
                                if (laptimeStorage.isPassedS1() && !laptimeStorage.isPassedS2()) {
                                    laptimeStorage.setPassedS2(true);
                                    laptimeStorage.setS2(System.currentTimeMillis());
                                    if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                                        player.sendMessage(ChatColor.GRAY + "Your time in S2 was " +  laptimeStorage.getS2Color() + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorDifference(), "ss.SS"));
                                    }
                                    raceDriver.getLaptimes(race).addSector();
                                    Bukkit.getLogger().warning(player.getDisplayName() + " s2");
                                    return;
                                }
                            }
                            if (storage.getS3().getCuboid().containsLocation(location)) {
                                if (!laptimeStorage.isPassedS3()) {
                                    if (laptimeStorage.isPassedS1() && laptimeStorage.isPassedS2()) {
                                        laptimeStorage.setS3(System.currentTimeMillis());
                                        if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                                            laptimeStorage.createLaptime();
                                            LaptimeStorage clone = laptimeStorage.clone();
                                            laptimeHash.put(raceDriver.getDriverUUID(), clone);
                                            raceDriver.addLaptime(race, clone);
                                            player.sendMessage(ChatColor.GRAY + "Your time in S3 was " +  laptimeStorage.getS3Color() + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorDifference(), "ss.SS"));
                                            player.sendMessage(ChatColor.GRAY + "Your lap time was a " + laptimeStorage.getLapColor(true) + Utils.millisToTimeString(laptimeStorage.getLaptime()) + " | " + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorDifference(), "ss.SS"));
                                            Bukkit.getLogger().warning(player.getDisplayName() + " s3");
                                        } else {
                                            player.sendMessage(ChatColor.RED + "Your lap time is INVALIDATED.");
                                            raceDriver.getLaptimes(race).setInvalidated(false);
                                        }
                                        raceDriver.getLaptimes(race).addSector();

                                        laptimeStorage.setPassedS1(false);
                                        laptimeStorage.setPassedS2(false);
                                        laptimeStorage.setPassedS3(true);

                                        raceDriver.setCurrentLap(raceDriver.getCurrentLap() + 1);
                                        if (raceMode.isLapped()) {
                                            if (raceDriver.getCurrentLap() >= race.getLaps()) {
                                                raceDriver.setFinished(true);
                                                final int position = finishers.size() + 1;
                                                finishers.put(position, raceDriver.getDriverUUID());

                                                player.sendMessage(ChatColor.GRAY + "You finished on position " + position);

                                                for (Player p : Bukkit.getOnlinePlayers()) {
                                                    if (Permissions.FIA_ADMIN.hasPermission(p) || Permissions.FIA_RACE.hasPermission(p)) {
                                                        p.sendMessage(player.getDisplayName() + " finished on position " + position);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        drivingBackwards(raceDriver, player);
                                    }
                                }
                            }
                        } else {
                            if (storage.getPitExit().getCuboid().containsLocation(location)) {
                                raceDriver.setPassedPitExit(race);
                                Bukkit.getLogger().warning(player.getDisplayName() + " pit out");
                            }
                        }
                    }
                } else {
                    MainVehicleListener.getRACE_DRIVERS().remove(raceDriver.getDriverUUID());
                }
            }
        }
    }

    public void reset() {
        laptimeHash.clear();
        bestLapTime = null;
        bestS1 = -1;
        bestS2 = -1;
        bestS3 = -1;
        MainVehicleListener.getRACE_DRIVERS().forEach((uuid, raceDriver) -> raceDriver.getLaptimes(race).resetLaptimes());
    }
}
