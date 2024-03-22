package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.UUID;

import static collinvht.f1mc.module.racing.object.race.RaceListener.drivingBackwards;

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
                        if (raceDriver.isPassedPitExit() || raceMode.isLapped()) {
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
                                        player.sendMessage(ChatColor.GRAY + "Your time in S1 was " +  laptimeStorage.getS1Color() + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorDifference(), "ss.SS"));
                                    }
                                    raceDriver.getLaptimes(race).addSector();
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
                                    return;
                                }
                            }
                            if (storage.getS3().getCuboid().containsLocation(location)) {
                                if (!laptimeStorage.isPassedS3()) {
                                    if (laptimeStorage.isPassedS1() && laptimeStorage.isPassedS2()) {
                                        laptimeStorage.setS3(System.currentTimeMillis());
                                        if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                                            player.sendMessage(ChatColor.GRAY + "Your time in S3 was " +  laptimeStorage.getS3Color() + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorDifference(), "ss.SS"));
                                            laptimeStorage.createLaptime();
                                            LaptimeStorage clone = laptimeStorage.copy();
                                            laptimeHash.put(raceDriver.getDriverUUID(), clone);
                                            raceDriver.addLaptime(race, clone);
                                            player.sendMessage(ChatColor.GRAY + "Your lap time was a " + laptimeStorage.getLapColor(true) + Utils.millisToTimeString(laptimeStorage.getLaptime()) + " | " + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorDifference(), "ss.SS"));
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
                                        drivingBackwards(raceDriver, race, player);
                                    }
                                }
                            }
                        }
                        if(!laptimeStorage.isPastPitExit()) {
                            if (storage.getPitExit().getCuboid().containsLocation(location)) {
                                raceDriver.setPassedPitExit(race);
                            }
                        }
                    }
                } else {
                    VPListener.getRACE_DRIVERS().remove(raceDriver.getDriverUUID());
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
        VPListener.getRACE_DRIVERS().forEach((uuid, raceDriver) -> raceDriver.getLaptimes(race).resetLaptimes());
    }
}
