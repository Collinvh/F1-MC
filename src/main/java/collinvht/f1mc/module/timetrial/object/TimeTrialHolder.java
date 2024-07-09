package collinvht.f1mc.module.timetrial.object;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.slowdown.obj.SlowdownBase;
import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.module.racing.module.tyres.obj.TyreBaseObject;
import collinvht.f1mc.module.racing.object.Cuboid;
import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.module.racing.object.race.ERSMode;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceCuboidStorage;
import collinvht.f1mc.module.timetrial.manager.TimeTrialManager;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Utils;
import com.mysql.cj.jdbc.MysqlDataSource;
import dev.lone.itemsadder.api.CustomBlock;
import io.netty.buffer.Unpooled;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.api.objects.spawn.SpawnMode;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.VehicleStats;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.Part;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.Seat;
import net.minecraft.network.PacketDataSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static collinvht.f1mc.module.racing.module.slowdown.manager.SlowdownManager.customslowDowns;
import static collinvht.f1mc.module.racing.module.slowdown.manager.SlowdownManager.slowDowns;

public class TimeTrialHolder {
    private final Player player;
    private final World world;
    private final Race race;
    private final RaceCuboidStorage storage;
    private final BaseVehicle vehicle;
    private final SpawnedVehicle spawnedVehicle;
    private final Location oldLocation;
    private RivalObject rival;
    private Seat seat;
    private ScheduledTask task;
    private boolean isRunning;
    private final TimeTrialLap timeTrialLap;
    private final String prefix = DefaultMessages.PREFIX;
    private boolean isInvalidated;
    private boolean isHotLapStart;
    private TyreBaseObject tyre;
    public TimeTrialHolder(Player player, Race race, BaseVehicle vehicle) {
        this.player = player;
        this.world = player.getWorld();
        this.race = race;
        this.isHotLapStart = true;
        this.storage = race.getStorage();
        this.vehicle = vehicle;
        this.timeTrialLap = TimeTrialLap.fromUUID(player.getUniqueId(), race.getName());
        this.timeTrialLap.setPassedS1(true);
        this.timeTrialLap.setPassedS2(true);
        this.oldLocation = player.getLocation();
        this.spawnedVehicle = VehiclesPlusAPI.getInstance().createVehicle(vehicle, player).spawnVehicle(storage.getTimeTrialSpawn(), SpawnMode.FORCE);
        this.rival = TimeTrialManager.getRivalObject(this.race.getName(), this.player.getUniqueId());
        if (this.rival != null) {
            if(this.rival.getRival() != null) {
                this.rival.setLap(race.getName());
            }
        }
        for (Part part : this.spawnedVehicle.getPartList()) {
            if(part instanceof Seat seat2) {
                this.seat = seat2;
            }
        }
        if(seat != null) {
            this.tyre = TyreManager.getTyres().get(TimeTrialManager.getTyrePreference().getOrDefault(player.getUniqueId(), "soft"));
            PacketDataSerializer wrappedBuffer = new PacketDataSerializer(Unpooled.buffer());
            wrappedBuffer.writeInt(spawnedVehicle.getHolder().getEntityId());

            player.sendPluginMessage(F1MC.getInstance(),  "formula:setcar", wrappedBuffer.array());
            start();
        }
    }

    public void start() {
        if (isRunning) {
            return;
        }
        this.player.teleport(storage.getTimeTrialSpawn());
        seat.enter(player);
        this.isRunning = true;
        this.task = F1MC.getAsyncScheduler().runAtFixedRate(F1MC.getInstance(), scheduledTask -> checkSectors(), 0, 1, TimeUnit.MILLISECONDS);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer != this.player) {
                hide(onlinePlayer);
            }
        }
    }
    public void stop() {
        if(!isRunning) {
            return;
        }
        task.cancel();
        player.teleport(oldLocation);
        if(spawnedVehicle.getStorageVehicle() != null) {
            if(VehiclesPlusAPI.getVehicleManager().getPlayerVehicleHashMap().containsKey(player.getUniqueId())) {
                spawnedVehicle.getStorageVehicle().removeVehicle(this.player);
            }
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(F1MC.getInstance(), this.player);
        }
        isRunning = false;
        //wrapper.setVisible(false);
    }

    public void updateTyre() {
        if (player == null) {
            return;
        }

        VehicleStats stats = spawnedVehicle.getStorageVehicle().getVehicleStats();

        if (tyre == null) {
            stats.setCurrentSpeed(0.0);
            stats.setSpeed(0);
            stats.setHighSpeedAcceleration(0.0f);
            stats.setLowSpeedAcceleration(0.0f);
            stats.setLowSpeedSteering(0.0f);
            stats.setHighSpeedSteering(0.0f);
        } else {
            float speedMultiplier = 1;
            stats.setSpeed((int) (vehicle.getSpeedSettings().getBase() + ERSMode.OVERTAKE.getExtraSpeed() + tyre.getExtraSpeed()));

            Block block = player.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock();
            float steering = vehicle.getTurningRadiusSettings().getLowSpeed();
            float steeringHigh = vehicle.getTurningRadiusSettings().getHighSpeed();
            float acceleration = vehicle.getAccelerationSettings().getLowSpeed();
            float accelerationHigh = vehicle.getAccelerationSettings().getHighSpeed();
            float braking = vehicle.getBrakeSettings().getBase();

            SlowdownBase slowdown = null;

            if (block.getType() == Material.NOTE_BLOCK) {
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                if (customBlock != null) {
                    slowdown = customslowDowns.get(customBlock.getNamespacedID());
                }
            } else if (slowDowns.containsKey(block.getType())) {
                slowdown = slowDowns.get(block.getType());
            }

            if (slowdown != null) {
                float steeringPercent = (float) slowdown.getSteeringPercent();
                steering *= steeringPercent;
                steeringHigh *= steeringPercent;
                acceleration *= steeringPercent * 2;
                accelerationHigh *= steeringPercent * 2;
                braking *= steeringPercent * 2;
            }

            stats.setBrakeForce(braking);
            stats.setLowSpeedSteering((float) (steering * tyre.getSteering() * speedMultiplier));
            stats.setHighSpeedSteering((float) (steeringHigh * tyre.getSteering() * speedMultiplier));
            stats.setLowSpeedAcceleration((float) (acceleration * tyre.getSteering() * speedMultiplier));
            stats.setHighSpeedAcceleration((float) (accelerationHigh * tyre.getSteering() * speedMultiplier));
        }
    }
    private void checkSectors() {
        updateTyre();
        Location location = spawnedVehicle.getHolder().getLocation();
        Cuboid s1 = storage.getS1().getCuboid();
        Cuboid s2 = storage.getS2().getCuboid();
        Cuboid s3 = storage.getS3().getCuboid();
        boolean hasRival = rival != null && rival.getLap() != null;
        if(!isInvalidated) {
            for (NamedCuboid cuboid : storage.getLimits().values()) {
                if (cuboid.getCuboid().containsVector(location.toVector())) {
                    isInvalidated = true;
                    player.sendMessage(prefix + ChatColor.RED + "You've invalidated your lap.");
                    PacketDataSerializer wrappedBuffer = new PacketDataSerializer(Unpooled.buffer());
                    player.sendPluginMessage(F1MC.getInstance(),  "formula:invalidatelap", wrappedBuffer.array());
                }
            }
        }
        if (s1.containsVector(location.toVector())) {
            if (!timeTrialLap.isPassedS1()) {
                timeTrialLap.setPassedS1(true);
                timeTrialLap.setPassedS3(false);
                timeTrialLap.setS1L(System.currentTimeMillis());
                long diff = hasRival ? rival.getLap().getS1().getSectorLength()-timeTrialLap.getS1().getSectorLength() : 0;
                if (!isInvalidated) {
                    PacketDataSerializer wrappedBuffer = new PacketDataSerializer(Unpooled.buffer());
                    if(hasRival) {
                        if(diff > 0 ) {
                            wrappedBuffer.writeInt(0);
                        } else {
                            wrappedBuffer.writeInt(-1);
                        }
                    } else {
                        wrappedBuffer.writeInt(0);
                    }
                    player.sendPluginMessage(F1MC.getInstance(),  "formula:completes1", wrappedBuffer.array());
                    player.sendMessage(prefix + ChatColor.GRAY + "Your S1 is " + ChatColor.RESET + Utils.millisToTimeString(timeTrialLap.getS1().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? ChatColor.RED : ChatColor.GREEN + "-") + Utils.millisToTimeString(diff) : ""));
                } else {
                    player.sendMessage(prefix + ChatColor.RED + "Your S1 is " + Utils.millisToTimeString(timeTrialLap.getS1().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? "" : "-") + Utils.millisToTimeString(diff) : ""));
                    player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid");
                }
                return;
            }
        }
        if (s2.containsVector(location.toVector())) {
            if (timeTrialLap.isPassedS1() && !timeTrialLap.isPassedS2()) {
                timeTrialLap.setPassedS2(true);
                timeTrialLap.setS2L(System.currentTimeMillis());
                long diff = hasRival ? rival.getLap().getS2().getSectorLength()-timeTrialLap.getS2().getSectorLength() : 0;
                if (!isInvalidated) {
                    PacketDataSerializer wrappedBuffer = new PacketDataSerializer(Unpooled.buffer());
                    if(hasRival) {
                        if(diff > 0 ) {
                            wrappedBuffer.writeInt(0);
                        } else {
                            wrappedBuffer.writeInt(-1);
                        }
                    } else {
                        wrappedBuffer.writeInt(0);
                    }
                    player.sendPluginMessage(F1MC.getInstance(),  "formula:completes2", wrappedBuffer.array());
                    player.sendMessage(prefix + ChatColor.GRAY + "Your S2 is " + ChatColor.RESET + Utils.millisToTimeString(timeTrialLap.getS2().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? ChatColor.RED : ChatColor.GREEN + "-") + Utils.millisToTimeString(diff) : ""));
                } else {
                    player.sendMessage(prefix + ChatColor.RED + "Your S2 is " + Utils.millisToTimeString(timeTrialLap.getS2().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? "" : "-") + Utils.millisToTimeString(diff) : ""));
                    player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid");
                }
                return;
            }
        }
        if (s3.containsVector(location.toVector())) {
            if (timeTrialLap.isPassedS1() && timeTrialLap.isPassedS2() && !timeTrialLap.isPassedS3()) {
                timeTrialLap.setPassedS3(true);
                timeTrialLap.setS3L(System.currentTimeMillis());
                if(!isHotLapStart) {

                    timeTrialLap.setLapL(timeTrialLap.getS1().getSectorLength() + timeTrialLap.getS2().getSectorLength() + timeTrialLap.getS3().getSectorLength());
                    long diff = hasRival ? rival.getLap().getS3().getSectorLength()-timeTrialLap.getS3().getSectorLength() : 0;
                    long diffLap = hasRival ? rival.getLap().getLapData().getSectorLength()-timeTrialLap.getLapData().getSectorLength() : 0;
                    if (!isInvalidated) {
                        PacketDataSerializer wrappedBuffer = new PacketDataSerializer(Unpooled.buffer());
                        if(hasRival) {
                            if(diff > 0 ) {
                                wrappedBuffer.writeInt(0);
                            } else {
                                wrappedBuffer.writeInt(-1);
                            }
                        } else {
                            wrappedBuffer.writeInt(0);
                        }
                        player.sendPluginMessage(F1MC.getInstance(),  "formula:completes2", wrappedBuffer.array());
                        player.sendMessage(prefix + ChatColor.GRAY + "Your S3 is " + ChatColor.RESET + Utils.millisToTimeString(timeTrialLap.getS3().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? ChatColor.RED : ChatColor.GREEN + "-") + Utils.millisToTimeString(diff) : ""));
                        player.sendMessage(prefix + ChatColor.GRAY + "Your lap time is " + ChatColor.RESET + Utils.millisToTimeString(timeTrialLap.getLapData().getSectorLength()) + (hasRival ? " | " + (diffLap < 0 ? ChatColor.RED : ChatColor.GREEN + "-") + Utils.millisToTimeString(diffLap) : ""));
                        dataBaseCheck();
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + "Your S3 is " + Utils.millisToTimeString(timeTrialLap.getS3().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? "" : "-") + Utils.millisToTimeString(diff) : ""));
                        player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid | " + Utils.millisToTimeString(timeTrialLap.getLapData().getSectorLength()) + (hasRival ? " | " + (diffLap < 0 ? "" : "-") + Utils.millisToTimeString(diffLap) : ""));
                        isInvalidated = false;
                    }
                } else isHotLapStart = false;
                timeTrialLap.setPassedS1(false);
                timeTrialLap.setPassedS2(false);
                timeTrialLap.setPassedS3(true);

                PacketDataSerializer wrappedBuffer2 = new PacketDataSerializer(Unpooled.buffer());
                wrappedBuffer2.writeLong(Instant.now().toEpochMilli());
                player.sendPluginMessage(F1MC.getInstance(),  "formula:startlap", wrappedBuffer2.array());
            }
        }
     }

    private void dataBaseCheck() {
        if(!player.hasPermission(vehicle.getPermissions().getRidePermission())) player.sendMessage(prefix + "You don't have the permissions for this car.");

        MysqlDataSource dataSource = Utils.getDatabase();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `player_uuid` = \""+ player.getUniqueId() +"\" AND `vehicle_name` = \""+ (vehicle.getPermissions().getRidePermission().contains("team.") ? "f1car" : vehicle.getPermissions().getRidePermission()) + "\" AND `track_name` = \"" + race.getName() + "\";");
            ResultSet rs = stmt.executeQuery();
            String id = null;
            long length = 0;
            if (rs.next()) {
                id = rs.getString("timetrial_id");
                length = rs.getLong("lap_length");
            }
            if(id != null) {
                if(length > timeTrialLap.getLapData().getSectorLength()) {
                    PreparedStatement nextStmt = connection.prepareStatement("UPDATE timetrial_laps SET `lap_length`=" + timeTrialLap.getLapData().getSectorLength() + ", `s1_length`=" + timeTrialLap.getS1().getSectorLength() + ", `s2_length`=" + timeTrialLap.getS2().getSectorLength() + ", `s3_length`=" + timeTrialLap.getS3().getSectorLength() +" WHERE `timetrial_id`=" + id + " AND `track_name` = \""+ race.getName() +"\";");
                    nextStmt.execute();
                    race.updateLeaderboard();
                    player.sendMessage(prefix + "New personal best!");
                    PreparedStatement lastStmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `track_name`= '"+ race.getName() +"' ORDER BY `lap_length` ASC;");
                    int number = 0;
                    long nextLength = -1;
                    String uuid = "";
                    ResultSet lastStmtRs = lastStmt.executeQuery();
                    while (lastStmtRs.next()) {
                        number +=1;
                        if(lastStmtRs.getString("player_uuid").equals(player.getUniqueId().toString())) {
                            break;
                        } else {
                            nextLength = lastStmtRs.getLong("lap_length");
                            uuid = lastStmtRs.getString("player_uuid");
                        }
                    }
                    if(uuid.isEmpty() || number == 0 || nextLength == -1) {
                        player.sendMessage(prefix + ChatColor.LIGHT_PURPLE + "You're currently at the top of the leaderboard");
                    } else {
                        player.sendMessage(prefix + (number > 0 ? "You're in p" + number + " behind " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + " " + Utils.millisToTimeString(nextLength - timeTrialLap.getLapData().getSectorLength()) : "You're currently at the top of the leaderboard"));
                    }
                }
            } else {
                PreparedStatement nextStmt = connection.prepareStatement("INSERT INTO timetrial_laps (`player_uuid`, `lap_length`, `s1_length`, `s2_length`, `s3_length`, `track_name`, `vehicle_name`) VALUES ('"+ player.getUniqueId() +"', "+ timeTrialLap.getLapData().getSectorLength() + ","+ timeTrialLap.getS1().getSectorLength() + ","+ timeTrialLap.getS2().getSectorLength() + ","+ timeTrialLap.getS3().getSectorLength() +",'" + race.getName() + "', '"+ (vehicle.getPermissions().getRidePermission().contains("team.") ? "f1car" : vehicle.getPermissions().getRidePermission()) + "');");
                nextStmt.execute();
                race.updateLeaderboard();
                player.sendMessage(prefix + "New personal best!");
                PreparedStatement lastStmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `track_name`= '"+ race.getName() +"' ORDER BY `lap_length` ASC;");
                int number = 0;
                long nextLength = -1;
                String uuid = "";
                ResultSet lastStmtRs = lastStmt.executeQuery();
                while (lastStmtRs.next()) {
                    number +=1;
                    if(lastStmtRs.getString("player_uuid").equals(player.getUniqueId().toString())) {
                        break;
                    } else {
                        nextLength = lastStmtRs.getLong("lap_length");
                        uuid = lastStmtRs.getString("player_uuid");
                    }
                }
                if(uuid.isEmpty() || number == 0 || nextLength == -1) {
                    player.sendMessage(prefix + ChatColor.LIGHT_PURPLE + "You're currently at the top of the leaderboard");
                } else {
                    player.sendMessage(prefix + (number > 0 ? "You're in p" + number + " behind " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + " " + Utils.millisToTimeString(nextLength - timeTrialLap.getLapData().getSectorLength()) : "You're currently at the top of the leaderboard"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Error adding laptime");
        }
    }

    public void hide(Player player) {
        player.hidePlayer(F1MC.getInstance(), this.player);
        for (Part part : spawnedVehicle.getPartList()) {
            player.hideEntity(F1MC.getInstance(), part.getHolder());
        }
    }

    public void deleteVehicle() {
        spawnedVehicle.despawn(true);
    }
}
