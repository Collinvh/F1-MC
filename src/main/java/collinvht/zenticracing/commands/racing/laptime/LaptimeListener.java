package collinvht.zenticracing.commands.racing.laptime;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.fia.Warning;
import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.commands.racing.laptime.object.Laptime;
import collinvht.zenticracing.commands.racing.object.CuboidStorage;
import collinvht.zenticracing.commands.racing.object.RaceMode;
import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.object.TeamObject;
import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import collinvht.zenticracing.listener.driver.object.FinishData;
import collinvht.zenticracing.manager.tyre.TyreData;
import collinvht.zenticracing.manager.tyre.TyreManager;
import collinvht.zenticracing.util.objs.Cuboid;
import collinvht.zenticracing.util.objs.DiscordUtil;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class LaptimeListener {

    @Getter
    private HashMap<UUID, Laptime> laptimeHash = new HashMap<>();

    private static final String prefix = ChatColor.DARK_RED + "FIA |" + ChatColor.RED + " Warn >> ";

    private int taskId;

    @Getter
    private Laptime bestLapTime;

    @Getter
    private long bestS1 = -1;

    @Getter
    private long prevBestS1 = -1;

    @Getter
    private long bestS2 = -1;

    @Getter
    private long prevBestS2 = -1;

    @Getter
    private long bestS3 = -1;

    @Getter @Setter
    private long prevBestS3 = -1;

    private int listenerID;

    private final RaceObject object;
    public LaptimeListener(RaceObject object) {
        this.object = object;
    }

    public void startTiming() {
        listenerID = new BukkitRunnable() {
            @Override
            public void run() {
                if(object.isRunning()) {
                    CuboidStorage storage = object.getStorage();
                    DriverManager.getDrivers().forEach((uuid, object1) -> {
                        DriverObject driver = DriverManager.getDriver(uuid);
                        Player player = driver.getPlayer();
                        SpawnedVehicle vehicle = driver.getCurvehicle();
                        if(player != null) {
                            if (object1.isDriving()) {
                                if (!object1.isBlackFlagged()) {
                                    Location location = player.getLocation();

                                    Laptime laptime = driver.getLapstorage().getCurrentLap();

                                    if(driver.getRaceStorage().isFinished()) return;

                                    if (laptime == null) {
                                        driver.getLapstorage().setCurrentLap(new Laptime(driver, object));
                                        laptime = driver.getLapstorage().getCurrentLap();
                                    }

                                    if (storage.getPit().containsLocation(location)) {
                                        if(vehicle.getCurrentSpeedInKm() > 60) {
                                            if(driver.getCurRunnable() == null) {
                                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                                    if(onlinePlayer.hasPermission("zentic.fia")) {
                                                        onlinePlayer.sendMessage(driver.getPlayer().getDisplayName() + " HAS SPEEDED IN PITLANE WITH" + vehicle.getCurrentSpeedInKm() + "KM/H");
                                                    }
                                                }


                                                BukkitRunnable runnable = new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        driver.setCurRunnable(null);
                                                    }
                                                };

                                                runnable.runTaskLater(ZenticRacing.getRacing(), 50L);


                                                driver.setCurRunnable(runnable);
                                            }
                                        }



                                        driver.getRaceStorage().setInPit(true);
                                    } else if(driver.getRaceStorage().isInPit()) {
                                        driver.getRaceStorage().setInPit(false);
                                    }

                                    storage.getDrsZone().forEach((s, drsZone) -> {
                                        if(!driver.isHasDRS()) {
                                            if(drsZone.getDetectieCuboid().containsLocation(location)) {
                                                if(object.getRunningMode().isHasLaps() && !driver.getRaceStorage().isPastDRSPoint()) {
                                                    long detectie = drsZone.getLastEntryOnDetectie();
                                                    if (detectie != -1) {
                                                        long time = detectie - System.currentTimeMillis();
                                                        if (time < 1000) {
                                                            driver.setHasDRS(true);
                                                        }
                                                    }
                                                    drsZone.setLastEntryOnDetectie(System.currentTimeMillis());
                                                    driver.getRaceStorage().setPastDRSPoint(true);
                                                } else {
                                                    driver.setHasDRS(true);
                                                }
                                            }
                                        }

                                        if(drsZone.getDrsStraight().containsLocation(location)) {
                                            driver.setInDrsZone(true);
                                        } else if(driver.isHadDRS()) {
                                            driver.setInDrsZone(false);
                                            driver.setHadDRS(false);
                                            driver.setHasDRS(false);
                                        }
                                    });

                                    if (storage.getPitexit().containsLocation(location) && !driver.getLapstorage().isPastPitExit()) {
                                        laptime.setS1s(System.currentTimeMillis());
                                        if (driver.getLapstorage().isPastS1()) {
                                            driver.getLapstorage().addSector();

                                            driver.getLapstorage().setPastS1(false);
                                            driver.getLapstorage().setPastS2(false);

                                            driver.getRaceStorage().setLap(driver.getRaceStorage().getLap() + 1);
                                        }

                                        if (driver.getLapstorage().isInvalidated()) {
                                            driver.getLapstorage().setInvalidated(false);
                                        }

                                        driver.getLapstorage().setPastPitExit(true);
                                    }

                                    if (storage.getS1().containsLocation(location) && !driver.getLapstorage().isPastS1()) {
                                        driver.getLapstorage().setPastS1(true);
                                        driver.getLapstorage().setPastPitExit(false);
                                        if (!driver.getLapstorage().isInvalidated()) {
                                            laptime.setS1(System.currentTimeMillis());

                                            player.sendMessage(ChatColor.GRAY + " Je tijd in sector 1 was " + laptime.getS1Color() + Laptime.millisToTimeString(laptime.getS1()) + "\n");
                                        }
                                        driver.getLapstorage().addSector();
                                    }

                                    if (storage.getS2().containsLocation(location) && driver.getLapstorage().isPastS1() && !driver.getLapstorage().isPastS2()) {
                                        driver.getLapstorage().setPastS2(true);
                                        if (!driver.getLapstorage().isInvalidated()) {
                                            laptime.setS2(System.currentTimeMillis());

                                            player.sendMessage(ChatColor.GRAY + " Je tijd in sector 2 was " + laptime.getS2Color() + Laptime.millisToTimeString(laptime.getS2()) + "\n");
                                        }
                                        driver.getLapstorage().addSector();
                                    }

                                    if (storage.getS3().containsLocation(location)) {
                                        laptime.setS1s(System.currentTimeMillis());
                                        if (driver.getLapstorage().isPastS1() && driver.getLapstorage().isPastS2()) {
                                            if (!driver.getLapstorage().isInvalidated()) {
                                                laptime.setS3(System.currentTimeMillis());
                                                player.sendMessage(ChatColor.GRAY + " Je tijd in sector 3 was " + laptime.getS3Color() + Laptime.millisToTimeString(laptime.getS3()) + "\n");

                                                if(vehicle.getBaseVehicle().getName().toLowerCase().contains("f1")) {
                                                    TeamObject teamObject = Team.checkTeamForPlayer(player);
                                                    if(teamObject != null) {
                                                        RaceCar car = teamObject.getRaceCarFromVehicle(vehicle);
                                                        if(car != null) {
                                                            ItemStack stack = car.getBandGui().getItem(13);
                                                            if(stack != null) {
                                                                TyreData data = TyreManager.getDataFromTyre(stack);
                                                                laptime.setTyre(data.getTyre());
                                                            }
                                                        }
                                                    }
                                                }

                                                laptime.createLaptime();
                                                Laptime clone = laptime.clone();
                                                laptimeHash.put(driver.getPlayerUUID(), clone);
                                                driver.getLapstorage().addLaptime(clone);

                                                player.sendMessage(ChatColor.GRAY + " Je laptijd was een " + laptime.getLapColor(true) + Laptime.millisToTimeString(laptime.getLaptime()) + "\n");
                                            } else {
                                                player.sendMessage(ChatColor.RED + " Je laptijd was INVALIDATED.");
                                                driver.getLapstorage().setInvalidated(false);
                                            }
                                            driver.getLapstorage().addSector();

                                            driver.getLapstorage().setPastS1(false);
                                            driver.getLapstorage().setPastS2(false);

                                            driver.getRaceStorage().setLap(driver.getRaceStorage().getLap() + 1);
                                            if (object.getRunningMode().isHasLaps()) {
                                                if (driver.getRaceStorage().getLap() >= object.getLapCount()) {
                                                    driver.getRaceStorage().setFinished(true);
                                                    FinishData data = new FinishData(driver, object.getFinishedDrivers().size() + 1);
                                                    object.addFinishedDriver(data);

                                                    player.sendMessage(ChatColor.GRAY + "Je bent gefinished op plek " + data.getFinishPosition());

                                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                                        if (p.hasPermission("zentic.fia.race") || p.hasPermission("zentic.admin")) {
                                                            p.sendMessage(player.getDisplayName() + " is gefinished op plek " + data.getFinishPosition());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    for (Cuboid detectie : storage.getDetecties().values()) {
                                        if (detectie.containsLocation(location)) {
                                            if (!driver.getLapstorage().isInvalidated()) {
                                                player.sendMessage(ChatColor.RED + " Je laptijd is geinvalidated.");
                                                driver.getLapstorage().setInvalidated(true);

                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        int maxspeed = vehicle.getStorageVehicle().getVehicleStats().getSpeed();
                                                        maxspeed *= 0.7F;
                                                        if(vehicle.getCurrentSpeedInKm() >= maxspeed) {
                                                            int warningCount = 0;
                                                            if(Warning.getWarningCount().get(uuid) != null) {
                                                                warningCount = Warning.getWarningCount().get(uuid);
                                                            }
                                                            warningCount++;

                                                            RaceMode mode = object.getRunningMode();
                                                            if(mode != null) {
                                                                if(mode.getWarningMargin() != -1) {
                                                                    if (warningCount >= mode.getWarningMargin()) {
                                                                        CommandUtil.sendMessageToServerWithPermission(prefix + "Speler heeft nu " + warningCount + " warns! Nu kun jij hem een penalty geven.", "zentic.fia");
                                                                    }
                                                                }
                                                            }
                                                            player.sendMessage(prefix + player.getDisplayName() + " | " + "Corner Cutting");
                                                            CommandUtil.sendMessageToServer(prefix + player.getDisplayName() + " | " + "Corner Cutting");

                                                            EmbedBuilder embedBuilder = new EmbedBuilder();
                                                            embedBuilder.setTitle("Waarschuwing", null);
                                                            embedBuilder.setColor(Color.RED);
                                                            embedBuilder.setDescription(player.getName());

                                                            embedBuilder.addField("Reden", "Corner Cutting || AUTOMATIC", false);
                                                            embedBuilder.addField("Warn Count", String.valueOf(warningCount), false);

                                                            embedBuilder.setFooter("ZenticRacing | " + object.getRaceName());

                                                            Warning.getWarningCount().put(uuid, warningCount);

                                                            DiscordUtil.getChannelByID(844159011666526208L).sendMessage(embedBuilder.build()).queue();
                                                        } else {
                                                            Bukkit.getLogger().warning("Speler heeft genoeg afgeremd geen automatic warn!");
                                                        }
                                                    }
                                                }.runTaskLater(ZenticRacing.getRacing(), 55L);
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    });
                }
            }
        }.runTaskTimer(ZenticRacing.getRacing(), 0, 1).getTaskId();
    }

    public void stopTiming() {
        Bukkit.getScheduler().cancelTask(listenerID);
        listenerID = 0;
    }

    public void setBestS1(long bestS1) {
        if(this.bestS1 != -1) {
            this.prevBestS1 = this.bestS1;
        }

        this.bestS1 = bestS1;
    }

    public void setBestS2(long bestS2) {
        if(this.bestS2 != -1) {
            this.prevBestS2 = this.bestS2;
        }

        this.bestS2 = bestS2;
    }

    public void setBestS3(long bestS3) {
        if(this.bestS3 != -1) {
            this.prevBestS3 = this.bestS3;
        }

        this.bestS3 = bestS3;
    }

    public void setBestLapTime(Laptime bestLapTime) {
        this.bestLapTime = bestLapTime;
    }
}
