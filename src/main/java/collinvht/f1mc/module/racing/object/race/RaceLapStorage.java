package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.discord.DiscordModule;
import collinvht.f1mc.module.racing.module.fia.command.commands.DSQCommand;
import collinvht.f1mc.module.racing.object.Cuboid;
import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.module.racing.object.laptime.DriverLaptimeStorage;
import collinvht.f1mc.module.racing.object.laptime.SectorData;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.Utils;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.N;
import scala.concurrent.impl.FutureConvertersImpl;

import java.awt.*;
import java.sql.Time;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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

    private static final String prefix = DefaultMessages.PREFIX;
    public RaceLapStorage(Race race) {
        this.race = race;
    }

    public void update(RaceDriver raceDriver) {
        if(raceDriver.isFinished()) return;
        Player player = Bukkit.getPlayer(raceDriver.getDriverUUID());
        if(player == null) return;
        if(raceDriver.isDisqualified())  {
            if(raceDriver.isInPit()) {
                if(raceDriver.getVehicle() == null) return;
                if(raceDriver.getVehicle().getCurrentSpeedInKm() > 140) {
                    if(raceDriver.getSpeedingFlags() >= 30) {
                        player.ban(prefix + "Speeding in the pits and continuing to do so", Duration.of(200, ChronoUnit.MINUTES), "F1-MC Plugin", true);
                        raceDriver.setSpeedingFlags(0);
                    } else {
                        raceDriver.setSpeedingFlags(raceDriver.getSpeedingFlags()+1);
                    }
                }
            } else {
                raceDriver.setSpeedingFlags(raceDriver.getSpeedingFlags()-1);
            }
            return;
        }
        if(!player.isOnline()) return;
        if(raceDriver.getVehicle() == null) return;
        DriverLaptimeStorage driverLaptimeStorage = raceDriver.getLaptimes(race);
        LaptimeStorage storage = driverLaptimeStorage.getCurrentLap();
        if(storage == null) {
            storage = new LaptimeStorage(raceDriver.getDriverUUID());
            storage.getS1().setSectorStart(System.currentTimeMillis()-100);
            driverLaptimeStorage.setCurrentLap(storage);
        }
        final LaptimeStorage laptimeStorage = storage;

        SpawnedVehicle spawnedVehicle = raceDriver.getVehicle();
        Location location = spawnedVehicle.getHolder().getLocation();

        if(!raceMode.isLapped()) {
            if(raceDriver.isInPit()) {
                Cuboid pitExit = race.getStorage().getPitExit().getCuboid();
                if(pitExit.containsLocation(location)) {
                    raceDriver.setPassedPitExit(race);
                } else {
                    if(spawnedVehicle.getCurrentSpeedInKm() > 80) {
                        if(spawnedVehicle.getCurrentSpeedInKm() > 130) {
                            raceDriver.setDisqualified(true);
                            player.sendMessage(prefix + "You've been disqualified.");
                            player.sendTitle(ChatColor.GRAY + "DSQ", "Speeding in the pits", 2, 50, 2);
                            if(Utils.isEnableDiscordModule()) {
                                DiscordModule discordModule = DiscordModule.getInstance();
                                if (discordModule.isInitialized()) {
                                    JDA jda = discordModule.getJda();
                                    TextChannel channel = jda.getTextChannelById(1217628051853021194L);
                                    if (channel != null) {
                                        EmbedBuilder embedBuilder = new EmbedBuilder();
                                        embedBuilder.setColor(Color.YELLOW);
                                        embedBuilder.setTitle("DSQ | " + player.getName());
                                        embedBuilder.addField("Reason", "Speeding in the pits by more than 40km/h", true);
                                        channel.sendMessage(embedBuilder.build()).queue();
                                    }
                                }
                            }
                            raceDriver.setInPit();
                            raceDriver.setSpeedingFlags(0);
                        } else {
                            if(raceDriver.getSpeedingFlags() >= 10) {
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(Permissions.FIA_ADMIN.hasPermission(onlinePlayer)) {
                                        onlinePlayer.sendMessage(prefix + player.getDisplayName() + " | Is speeding in the pits\nCircuit: " + race.getName() + "\nSpeed: " + spawnedVehicle.getCurrentSpeedInKm() + "/80km/h");
                                    }
                                }
                                raceDriver.setSpeedingFlags(0);
                            } else {
                                raceDriver.setSpeedingFlags(raceDriver.getSpeedingFlags()+1);
                            }
                        }
                    } else {
                        raceDriver.setSpeedingFlags(raceDriver.getSpeedingFlags()-1);
                    }
                    return;
                }
            }
            if(raceDriver.getSpeedingFlags() > 0) raceDriver.setSpeedingFlags(raceDriver.getSpeedingFlags()-1);
            Cuboid pitEntry = race.getStorage().getPitEntry().getCuboid();
            if(pitEntry.containsLocation(location)) {
                raceDriver.setInPit();
                return;
            }
        }
        if(!driverLaptimeStorage.isInvalidated()) {
            for (NamedCuboid cuboid : race.getStorage().getLimits().values()) {
                if (cuboid.getCuboid().containsLocation(location)) {
                    driverLaptimeStorage.setInvalidated(true);
                    player.sendMessage(prefix + ChatColor.RED + "You've invalidated your lap.");
                }
            }
        }
        if(!laptimeStorage.isPassedS1()) {
            Cuboid s1 = race.getStorage().getS1().getCuboid();
            race.getStorage().getS1_mini().forEach((s, namedCuboid) -> {
                if(!laptimeStorage.getCuboids().contains(namedCuboid)) {
                    if(namedCuboid.getCuboid().containsLocation(location)) {
                        SectorData data = new SectorData(raceDriver.getDriverUUID());
                        if(!laptimeStorage.getCurrentMini().isEmpty()) {
                            final long current = System.currentTimeMillis();
                            laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                            player.sendMessage(prefix + "Mini " + laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).getSectorName()  + " | " + Utils.millisToTimeString(laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
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
                if(!laptimeStorage.getCurrentMini().isEmpty()) {
                    laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                    player.sendMessage(prefix + "Mini " + laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).getSectorName()  + " | " + Utils.millisToTimeString(laptimeStorage.getS1_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                    laptimeStorage.setCurrentMini("");
                }
                if (!laptimeStorage.isPassedS1()) {
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
        } else if(!laptimeStorage.isPassedS2()) {
            Cuboid s2 = race.getStorage().getS2().getCuboid();
            race.getStorage().getS2_mini().forEach((s, namedCuboid) -> {
                if(!laptimeStorage.getCuboids().contains(namedCuboid)) {
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
                if(!laptimeStorage.getS2_minis().isEmpty()) {
                    laptimeStorage.getS2_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                    player.sendMessage(prefix + "Mini " + (laptimeStorage.getS2_minis().size()) + " | " + Utils.millisToTimeString(laptimeStorage.getS2_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                    laptimeStorage.setCurrentMini("");
                }
                if (laptimeStorage.isPassedS1() && !laptimeStorage.isPassedS2()) {
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
                if(!laptimeStorage.getCuboids().contains(namedCuboid)) {
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
                if(!laptimeStorage.getS3_minis().isEmpty()) {
                    laptimeStorage.getS3_minis().get(laptimeStorage.getCurrentMini()).setSectorLengthL(current);
                    player.sendMessage(prefix + "Mini " + (laptimeStorage.getS3_minis().size()) + " | " + Utils.millisToTimeString(laptimeStorage.getS3_minis().get(laptimeStorage.getCurrentMini()).getSectorLength()));
                    laptimeStorage.setCurrentMini("");
                }
                if (laptimeStorage.isPassedS1() && laptimeStorage.isPassedS2() && !laptimeStorage.isPassedS3()) {
                    laptimeStorage.setPassedS3(true);
                    laptimeStorage.setS3L(current);
                    laptimeStorage.setLapL(laptimeStorage.getS1().getSectorLength() + laptimeStorage.getS2().getSectorLength() + laptimeStorage.getS3().getSectorLength());
                    if (!driverLaptimeStorage.isInvalidated()) {
                        player.sendMessage(prefix + ChatColor.GRAY + "Your S3 is " + ChatColor.RESET + Utils.millisToTimeString(laptimeStorage.getS3().getSectorLength()));
                        player.sendMessage(prefix + ChatColor.GRAY + "Your lap time is " + ChatColor.RESET + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorLength()));
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + "Your S3 is " + Utils.millisToTimeString(laptimeStorage.getS3().getSectorLength()));
                        player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid | " + Utils.millisToTimeString(laptimeStorage.getLapData().getSectorLength()));
                        driverLaptimeStorage.setInvalidated(false);
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
        VPListener.getRACE_DRIVERS().forEach((uuid, raceDriver) -> raceDriver.getLaptimes(race).resetLaptimes());
    }
}
