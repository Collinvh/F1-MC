package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTypes;
import collinvht.f1mc.module.racing.object.Cuboid;
import collinvht.f1mc.module.racing.object.PenaltyCuboid;
import collinvht.f1mc.module.racing.object.laptime.DriverLaptimeStorage;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.module.racing.object.laptime.SectorData;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.Utils;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class RaceLapStorage {
    @Getter
    private final Race race;

    @Getter
    @Setter
    private RaceMode raceMode;

    @Getter
    @Setter
    private WeatherTypes weatherType = WeatherTypes.DRY;


    @Getter
    @Setter
    private WeatherTypes nextWeatherType = WeatherTypes.DRY;

    @Getter
    @Setter
    private int waterPercentage = WeatherTypes.DRY.getWaterPercentage();

    @Getter
    private final LinkedHashMap<UUID, LaptimeStorage> laptimeHash = new LinkedHashMap<>();

    @Getter
    private final LinkedHashMap<Integer, RaceDriver> finishers = new LinkedHashMap<>();

    @Getter
    @Setter
    private LaptimeStorage bestLapTime;

    private final Timer timer = new Timer("f1mc.finishTimer");

    @Getter
    @Setter
    private long bestS1 = -1;

    @Getter
    @Setter
    private long bestS2 = -1;

    @Getter
    @Setter
    private long bestS3 = -1;

    private static final String prefix = DefaultMessages.PREFIX;

    public RaceLapStorage(Race race) {
        this.race = race;
    }

    public void update(RaceDriver raceDriver) {
        if (raceDriver == null) return;
        if (raceDriver.getDriverUUID() == null) return;
        Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
        if (player == null) return;
        if (!player.isOnline()) {
            Bukkit.getLogger().warning("PLAYER OFFLINE + " + player.getName());
        }
        if (raceDriver.getVehicle() == null) {
            return;
        }
        if (raceDriver.isFinished()) {
            return;
        }
        if (raceDriver.isDisqualified()) {
            return;
        }
        if (RaceManager.getDrivingPlayers().get(player) != null) {
            if (RaceManager.getDrivingPlayers().get(player) != race) {
                RaceManager.getDrivingPlayers().put(player, race);
            }
        } else {
            RaceManager.getDrivingPlayers().put(player, race);
        }


        DriverLaptimeStorage driverLaptimeStorage = raceDriver.getLaptimes(race);
        LaptimeStorage storage = driverLaptimeStorage.getCurrentLap();
        if (storage == null) {
            storage = new LaptimeStorage(raceDriver.getDriverUUID());
            storage.getS1().setSectorStart((System.currentTimeMillis() - 5000L));
            driverLaptimeStorage.setCurrentLap(storage);
        }
        final LaptimeStorage laptimeStorage = storage;

        SpawnedVehicle spawnedVehicle = raceDriver.getVehicle();
        Location location = spawnedVehicle.getHolder().getLocation();
        if (raceDriver.isInPit()) {
            Cuboid pitExit = race.getStorage().getPitExit().getCuboid();
            if (pitExit.containsLocation(location)) {
                raceDriver.setPassedPitExit(race);
                Bukkit.getLogger().warning("pit out");
            }
        } else {
            Cuboid pitEntry = race.getStorage().getPitEntry().getCuboid();
            if (pitEntry.containsLocation(location)) {
                raceDriver.setInPit();
                Bukkit.getLogger().warning("pit in");
                return;
            }
        }
        if (raceMode == RaceMode.NO_TIMING) return;
        boolean hadFlag = false;
        for (PenaltyCuboid cuboid : race.getStorage().getLimits().values()) {
            if (cuboid.getCuboid().containsLocation(location)) {
                if (!driverLaptimeStorage.isInvalidated()) {
                    driverLaptimeStorage.setInvalidated(true);
                    driverLaptimeStorage.setInvalidFlags(driverLaptimeStorage.getInvalidFlags() + 5);
                    player.sendMessage(prefix + ChatColor.RED + "You've invalidated your lap.");
                }
                if (raceMode.isLapped()) {
                    if (!hadFlag) {
                        hadFlag = true;
                        int curSpeed = spawnedVehicle.getCurrentSpeedInKm();
                        if (curSpeed >= 90) {
                            if (curSpeed >= 140) {
                                driverLaptimeStorage.setInvalidFlags(driverLaptimeStorage.getInvalidFlags() + 4 + cuboid.getExtraFlags());
                            } else {
                                driverLaptimeStorage.setInvalidFlags(driverLaptimeStorage.getInvalidFlags() + 2 + cuboid.getExtraFlags());
                            }
                        } else if (curSpeed >= 25) {
                            driverLaptimeStorage.setInvalidFlags(driverLaptimeStorage.getInvalidFlags() + 1 + cuboid.getExtraFlags());
                        }
                    }
                    if (driverLaptimeStorage.getInvalidFlags() > 2500) {
                        driverLaptimeStorage.setInvalidFlags(-500);
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.sendMessage(prefix + player.getName() + " +" + ChatColor.RED + "3s penalty\nREASON: Corner cutting");
                        }
                        player.sendMessage(prefix + ChatColor.RED + "You've gotten a 3s penalty for corner cutting.");
                        driverLaptimeStorage.setPenalty(driverLaptimeStorage.getPenalty() + 3);
                    }
                }
            }
        }

        if (!laptimeStorage.isPassedS1()) {
            Cuboid s1 = race.getStorage().getS1().getCuboid();
            race.getStorage().getS1_mini().forEach((s, namedCuboid) -> {
                if (!laptimeStorage.getCuboids().contains(namedCuboid)) {
                    if (namedCuboid.getCuboid().containsLocation(location)) {
                        SectorData data = new SectorData(raceDriver.getDriverUUID());
                        if (!laptimeStorage.getCurrentMini().isEmpty()) {
                            final long current = System.currentTimeMillis();
                            laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                            player.sendMessage(prefix + "Mini " + laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).getSectorName() + " | " + Utils.millisToTimeString(laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                            data.setSectorStart(current);
                        } else {
                            data.setSectorStart(laptimeStorage.getS1().getSectorStart());
                        }
                        data.setSectorName(s);
                        laptimeStorage.getS1_minis().put(s, data);
                        laptimeStorage.getCuboids().add(namedCuboid);
                        laptimeStorage.setCurrentMini(s);
                    }
                }
            });

            if (s1.containsLocation(location)) {
                final long current = System.currentTimeMillis();
                if (!laptimeStorage.getCurrentMini().isEmpty()) {
                    laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                    player.sendMessage(prefix + "Mini " + laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).getSectorName() + " | " + Utils.millisToTimeString(laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                    laptimeStorage.setCurrentMini("");
                }
                if (!laptimeStorage.isPassedS1()) {
                    if (driverLaptimeStorage.getInvalidFlags() > 250) {
                        driverLaptimeStorage.setInvalidFlags(driverLaptimeStorage.getInvalidFlags() / 2);
                    } else {
                        driverLaptimeStorage.setInvalidFlags(0);
                    }
                    driverLaptimeStorage.addSector();
                    laptimeStorage.setPassedS1(true);
                    laptimeStorage.setPassedS3(false);
                    laptimeStorage.setS1L(current);
                    if (!driverLaptimeStorage.isInvalidated()) {
                        player.sendMessage(prefix + ChatColor.GRAY + "Your S1 is " + ChatColor.RESET + Utils.millisToTimeString(laptimeStorage.getS1().getSectorLength()));
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + "Your S1 is " + Utils.millisToTimeString(laptimeStorage.getS1().getSectorLength()));
                        player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid");
                    }
                }
            }
        } else if (!laptimeStorage.isPassedS2()) {
            Cuboid s2 = race.getStorage().getS2().getCuboid();
            race.getStorage().getS2_mini().forEach((s, namedCuboid) -> {
                if (!laptimeStorage.getCuboids().contains(namedCuboid)) {
                    if (namedCuboid.getCuboid().containsLocation(location)) {
                        SectorData data = new SectorData(raceDriver.getDriverUUID());
                        if (!laptimeStorage.getCurrentMini().isEmpty()) {
                            final long current = System.currentTimeMillis();
                            laptimeStorage.getS2_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                            player.sendMessage(prefix + "Mini " + laptimeStorage.getS2_minis().get(laptimeStorage.getCurrentMini()).getSectorName() + " | " + Utils.millisToTimeString(laptimeStorage.getS2_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                            data.setSectorStart(current);
                        } else {
                            data.setSectorStart(laptimeStorage.getS2().getSectorStart());
                        }
                        data.setSectorName(s);
                        laptimeStorage.getS2_minis().put(s, data);
                        laptimeStorage.getCuboids().add(namedCuboid);
                        laptimeStorage.setCurrentMini(s);
                    }
                }
            });

            if (s2.containsLocation(location)) {
                final long current = System.currentTimeMillis();
                if (!laptimeStorage.getS2_minis().isEmpty()) {
                    laptimeStorage.getS2_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                    player.sendMessage(prefix + "Mini " + (laptimeStorage.getS2_minis().size()) + " | " + Utils.millisToTimeString(laptimeStorage.getS2_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                    laptimeStorage.setCurrentMini("");
                }
                if (laptimeStorage.isPassedS1() && !laptimeStorage.isPassedS2()) {
                    if (driverLaptimeStorage.getInvalidFlags() > 25) {
                        driverLaptimeStorage.setInvalidFlags(driverLaptimeStorage.getInvalidFlags() / 2);
                    } else {
                        driverLaptimeStorage.setInvalidFlags(0);
                    }
                    driverLaptimeStorage.addSector();
                    laptimeStorage.setPassedS2(true);
                    laptimeStorage.setS2L(current);
                    if (!driverLaptimeStorage.isInvalidated()) {
                        player.sendMessage(prefix + ChatColor.GRAY + "Your S2 is " + ChatColor.RESET + Utils.millisToTimeString(laptimeStorage.getS2().getSectorLength()));
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + "Your S2 is " + Utils.millisToTimeString(laptimeStorage.getS2().getSectorLength()));
                        player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid");
                    }
                }
            }
        } else {
            Cuboid s3 = race.getStorage().getS3().getCuboid();
            race.getStorage().getS3_mini().forEach((s, namedCuboid) -> {
                if (!laptimeStorage.getCuboids().contains(namedCuboid)) {
                    if (namedCuboid.getCuboid().containsLocation(location)) {
                        SectorData data = new SectorData(raceDriver.getDriverUUID());
                        if (!laptimeStorage.getCurrentMini().isEmpty()) {
                            final long current = System.currentTimeMillis();
                            laptimeStorage.getS3_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                            player.sendMessage(prefix + "Mini " + laptimeStorage.getS3_minis().get(laptimeStorage.getCurrentMini()).getSectorName() + " | " + Utils.millisToTimeString(laptimeStorage.getS3_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                            data.setSectorStart(current);
                        } else {
                            data.setSectorStart(laptimeStorage.getS3().getSectorStart());
                        }
                        data.setSectorName(s);
                        laptimeStorage.getS3_minis().put(s, data);
                        laptimeStorage.getCuboids().add(namedCuboid);
                        laptimeStorage.setCurrentMini(s);
                    }
                }
            });

            if (s3.containsLocation(location)) {
                final long current = System.currentTimeMillis();
                if (!laptimeStorage.getS3_minis().isEmpty()) {
                    laptimeStorage.getS3_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                    player.sendMessage(prefix + "Mini " + (laptimeStorage.getS3_minis().size()) + " | " + Utils.millisToTimeString(laptimeStorage.getS3_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                    laptimeStorage.setCurrentMini("");
                }
                if (laptimeStorage.isPassedS1() && laptimeStorage.isPassedS2() && !laptimeStorage.isPassedS3()) {
                    driverLaptimeStorage.addSector();
                    if (driverLaptimeStorage.getInvalidFlags() > 25) {
                        driverLaptimeStorage.setInvalidFlags(driverLaptimeStorage.getInvalidFlags() / 2);
                    } else {
                        driverLaptimeStorage.setInvalidFlags(0);
                    }
                    laptimeStorage.setPassedS3(true);
                    laptimeStorage.setS3L(current);
                    laptimeStorage.setLapL(laptimeStorage.getS1().getSectorLength() + laptimeStorage.getS2().getSectorLength() + laptimeStorage.getS3().getSectorLength());
                    if (!driverLaptimeStorage.isInvalidated()) {
                        player.sendMessage(prefix + ChatColor.GRAY + "Your S3 is " + ChatColor.RESET + Utils.millisToTimeString(laptimeStorage.getS3().getSectorLength()));
                        player.sendMessage(prefix + ChatColor.GRAY + "Your lap time is " + ChatColor.RESET + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorLength()));
                        driverLaptimeStorage.addLaptime(storage.copy(), raceMode);
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + "Your S3 is " + Utils.millisToTimeString(laptimeStorage.getS3().getSectorLength()));
                        player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid | " + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorLength()));
                        driverLaptimeStorage.setInvalidated(false);
                    }
                    raceDriver.setCurrentLap(raceDriver.getCurrentLap() + 1);
                    if (raceMode.isLapped()) {
                        if (raceDriver.getCurrentLap() >= race.getLaps()) {
                            if (driverLaptimeStorage.getPenalty() > 0) {
                                player.sendMessage(prefix + ChatColor.RED + "You've finished but you got a penalty\n It'll show up soon!");
                                raceDriver.setFinished(true);
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        final int position = finishers.size() + 1;
                                        finishers.put(position, raceDriver);

                                        player.sendMessage(prefix + ChatColor.GRAY + "You finished on position " + position);

                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            if (Permissions.FIA_ADMIN.hasPermission(p) || Permissions.FIA_RACE.hasPermission(p)) {
                                                p.sendMessage(player.getDisplayName() + " finished on position " + position);
                                            }
                                        }
                                        raceDriver.setFinishTime(System.currentTimeMillis());
                                    }
                                }, (1000L * ((long)driverLaptimeStorage.getPenalty())));
                                return;
                            } else {
                                raceDriver.setFinished(true);
                                final int position = finishers.size() + 1;
                                finishers.put(position, raceDriver);

                                player.sendMessage(ChatColor.GRAY + "You finished on position " + position);

                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (Permissions.FIA_ADMIN.hasPermission(p) || Permissions.FIA_RACE.hasPermission(p)) {
                                        p.sendMessage(player.getDisplayName() + " finished on position " + position);
                                    }
                                }
                                raceDriver.setFinishTime(System.currentTimeMillis());
                            }
                        }
                    }

                    laptimeStorage.getCuboids().clear();
                    laptimeStorage.getS1_minis().clear();
                    laptimeStorage.getS2_minis().clear();
                    laptimeStorage.getS3_minis().clear();
                    laptimeStorage.setPassedS1(false);
                    laptimeStorage.setPassedS2(false);
                    laptimeStorage.setPassedS3(true);
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
        finishers.clear();
        VPListener.getRACE_DRIVERS().forEach((uuid, raceDriver) -> {
            raceDriver.setCurrentLap(0);
            raceDriver.setDisqualified(false);
            raceDriver.setSpeedingFlags(0);
            raceDriver.setFinished(false);
            raceDriver.getLaptimes(race).resetLaptimes();
        });
    }
}