package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.module.racing.object.laptime.SectorData;
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
import org.checkerframework.checker.units.qual.N;
import scala.concurrent.impl.FutureConvertersImpl;

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
        Player player = getPlayer(raceDriver);
        if(player == null) return;
        if(raceDriver.getVehicle() == null) return;
        if(raceDriver.isFinished()) return;
        RaceCuboidStorage storage = race.getStorage();
        Location location = raceDriver.getVehicle().getHolder().getLocation();

        LaptimeStorage laptimeStorage = raceDriver.getLaptimes(race).getCurrentLap();
        if (raceDriver.isPassedPitExit() || raceMode.isLapped()) {
            if (storage.getPitEntry().contains(location)) {
                raceDriver.setInPit();
            }

            if(!laptimeStorage.isPassedS1()) {
                for (NamedCuboid passableNamedCuboid : storage.getS1_mini().values()) {
                    if(passableNamedCuboid.contains(location)) {
                        if (!laptimeStorage.getS1_minis().isEmpty()) {
                            int count = laptimeStorage.getS1_minis().size();
                            SectorData oldData = laptimeStorage.getS1_minis().get(count);
                            oldData.setSectorLengthL(System.currentTimeMillis());
                            SectorData data = new SectorData(raceDriver.getDriverUUID());
                            data.setSectorStart(System.currentTimeMillis());
                            laptimeStorage.getS1_minis().put(count + 1, data);
                            player.sendMessage("test: " + Utils.millisToTimeString(oldData.getSectorDifference()));
                        } else {
                            SectorData data = new SectorData(raceDriver.getDriverUUID());
                            data.setSectorStart(System.currentTimeMillis());
                            laptimeStorage.getS1_minis().put(0, data);
                            player.sendMessage("test: " + "1");
                        }
                    }
                }

                if (storage.getS1().contains(location)) {
                    if(!storage.getS1_mini().isEmpty()) {
                        laptimeStorage.getS1_minis().get(laptimeStorage.getS1_minis().size()).setSectorLengthL(System.currentTimeMillis());
                    }

                    laptimeStorage.setPassedS1(true);
                    laptimeStorage.setPassedS3(false);
                    laptimeStorage.setS1(System.currentTimeMillis());
                    if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                        player.sendMessage(ChatColor.GRAY + "Your S1 is " +  laptimeStorage.getS1Color() + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorDifference(), "ss.SS"));
                    }
                    raceDriver.getLaptimes(race).addSector();
                }
            } else if(!laptimeStorage.isPassedS2()) {
                for (NamedCuboid passableNamedCuboid : storage.getS2_mini().values()) {
                    if(passableNamedCuboid.contains(location)) {
                        if (!laptimeStorage.getS2_minis().isEmpty()) {
                            int count = laptimeStorage.getS2_minis().size();
                            SectorData oldData = laptimeStorage.getS2_minis().get(count);
                            oldData.setSectorLengthL(System.currentTimeMillis());
                            SectorData data = new SectorData(raceDriver.getDriverUUID());
                            data.setSectorStart(System.currentTimeMillis());
                            laptimeStorage.getS2_minis().put(count + 1, data);
                            player.sendMessage("test: " + Utils.millisToTimeString(oldData.getSectorDifference()));
                        } else {
                            SectorData data = new SectorData(raceDriver.getDriverUUID());
                            data.setSectorStart(System.currentTimeMillis());
                            laptimeStorage.getS2_minis().put(0, data);
                        }
                    }
                }

                if (storage.getS2().contains(location)) {
                    if(!storage.getS2_mini().isEmpty()) {
                        laptimeStorage.getS2_minis().get(laptimeStorage.getS2_minis().size()).setSectorLengthL(System.currentTimeMillis());
                    }
                    if (laptimeStorage.isPassedS1() && !laptimeStorage.isPassedS2()) {
                        laptimeStorage.setPassedS2(true);
                        laptimeStorage.setS2(System.currentTimeMillis());
                        if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                            player.sendMessage(ChatColor.GRAY + "Your S2 is " +  laptimeStorage.getS2Color() + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorDifference(), "ss.SS"));
                        }
                        raceDriver.getLaptimes(race).addSector();
                    }
                }
            } else {
                for (NamedCuboid passableNamedCuboid : storage.getS3_mini().values()) {
                    if(passableNamedCuboid.contains(location)) {
                        if (!laptimeStorage.getS3_minis().isEmpty()) {
                            int count = laptimeStorage.getS3_minis().size();
                            SectorData oldData = laptimeStorage.getS3_minis().get(count);
                            oldData.setSectorLengthL(System.currentTimeMillis());
                            SectorData data = new SectorData(raceDriver.getDriverUUID());
                            data.setSectorStart(System.currentTimeMillis());
                            laptimeStorage.getS3_minis().put(count + 1, data);
                            player.sendMessage("test: " + Utils.millisToTimeString(oldData.getSectorDifference()));
                        } else {
                            SectorData data = new SectorData(raceDriver.getDriverUUID());
                            data.setSectorStart(System.currentTimeMillis());
                            laptimeStorage.getS3_minis().put(0, data);
                        }
                    }
                }

                if (storage.getS3().contains(location)) {
                    if(!storage.getS3_mini().isEmpty()) {
                        laptimeStorage.getS3_minis().get(laptimeStorage.getS3_minis().size()).setSectorLengthL(System.currentTimeMillis());
                    }
                    laptimeStorage.setS3(System.currentTimeMillis());
                    if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                        player.sendMessage(ChatColor.GRAY + "Your S3 is " +  laptimeStorage.getS3Color() + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorDifference(), "ss.SS"));
                        laptimeStorage.createLaptime();
                        LaptimeStorage clone = laptimeStorage.copy();
                        laptimeHash.put(raceDriver.getDriverUUID(), clone);
                        raceDriver.addLaptime(race, raceMode, clone);
                        player.sendMessage(ChatColor.GRAY + "Your lap time is " + laptimeStorage.getLapColor(true) + Utils.millisToTimeString(laptimeStorage.getLaptime()) + " | " + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorDifference(), "ss.SS"));
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
                }
            }
        } else {
            if (storage.getPitExit().contains(location)) {
                raceDriver.setPassedPitExit(race);
            }
        }
    }

    private Player getPlayer(RaceDriver raceDriver) {
        if(!raceDriver.isDriving()) return null;
        Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
        if(player == null) return null;
        if(!player.isOnline()) return null;
        return player;
    }

    public void updateOld(RaceDriver raceDriver) {
        if(raceDriver.isDriving()) {
            Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
            if(player != null) {
                if (player.isOnline()) {
                    RaceCuboidStorage storage = race.getStorage();
                    LaptimeStorage laptimeStorage = raceDriver.getLaptimes(race).getCurrentLap();
                    if (raceDriver.getVehicle() != null) {
                        if (laptimeStorage == null) {
                            laptimeStorage = new LaptimeStorage(raceDriver, race);
                            if(raceMode == RaceMode.TIMETRIAL) {
                                laptimeStorage.getS1data().setSectorStart(System.currentTimeMillis()-100);
                            }
                            raceDriver.getLaptimes(race).setCurrentLap(laptimeStorage);
                        }
                        Location location = player.getLocation();
                        if (raceDriver.isPassedPitExit() || raceMode.isLapped() || raceMode == RaceMode.TIMETRIAL) {
                            if(!raceDriver.getLaptimes(race).isInvalidated()) {
                                for (NamedCuboid cuboid : storage.getLimits().values()) {
                                    if (cuboid.getCuboid().containsLocation(location)) {
                                        raceDriver.getLaptimes(race).setInvalidated(true);
                                        player.sendMessage(ChatColor.RED + "You've invalidated your lap.");
                                    }
                                }
                            }
                            if (!raceDriver.isInPit() && raceMode != RaceMode.TIMETRIAL) {
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
                                        player.sendMessage(ChatColor.GRAY + "Your S1 is " +  laptimeStorage.getS1Color() + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS1data().getSectorDifference(), "ss.SS"));
                                    }
                                    if(raceMode != RaceMode.TIMETRIAL) raceDriver.getLaptimes(race).addSector();
                                    return;
                                }
                            }
                            if (storage.getS2().getCuboid().containsLocation(location)) {
                                if (laptimeStorage.isPassedS1() && !laptimeStorage.isPassedS2()) {
                                    laptimeStorage.setPassedS2(true);
                                    laptimeStorage.setS2(System.currentTimeMillis());
                                    if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                                        player.sendMessage(ChatColor.GRAY + "Your S2 is " +  laptimeStorage.getS2Color() + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS2data().getSectorDifference(), "ss.SS"));
                                    }
                                    if(raceMode != RaceMode.TIMETRIAL) raceDriver.getLaptimes(race).addSector();
                                    return;
                                }
                            }
                            if (storage.getS3().getCuboid().containsLocation(location)) {
                                if (!laptimeStorage.isPassedS3()) {
                                    if (laptimeStorage.isPassedS1() && laptimeStorage.isPassedS2()) {
                                        laptimeStorage.setS3(System.currentTimeMillis());
                                        if (!raceDriver.getLaptimes(race).isInvalidated() && !raceDriver.isDisqualified()) {
                                            player.sendMessage(ChatColor.GRAY + "Your S3 is " +  laptimeStorage.getS3Color() + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorLength()) + " | " + Utils.millisToTimeString(laptimeStorage.getS3data().getSectorDifference(), "ss.SS"));
                                            laptimeStorage.createLaptime();
                                            LaptimeStorage clone = laptimeStorage.copy();
                                            laptimeHash.put(raceDriver.getDriverUUID(), clone);
                                            raceDriver.addLaptime(race, raceMode, clone);
                                            player.sendMessage(ChatColor.GRAY + "Your lap time is " + laptimeStorage.getLapColor(true) + Utils.millisToTimeString(laptimeStorage.getLaptime()) + " | " + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorDifference(), "ss.SS"));
                                        } else {
                                            player.sendMessage(ChatColor.RED + "Your lap time is INVALIDATED.");
                                            raceDriver.getLaptimes(race).setInvalidated(false);
                                        }
                                        if(raceMode != RaceMode.TIMETRIAL) raceDriver.getLaptimes(race).addSector();

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
