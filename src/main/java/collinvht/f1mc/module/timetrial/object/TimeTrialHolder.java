package collinvht.f1mc.module.timetrial.object;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.object.Cuboid;
import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceCuboidStorage;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import collinvht.f1mc.module.timetrial.manager.TimeTrialManager;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Utils;
import com.mysql.cj.jdbc.MysqlDataSource;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.PlayerCustomHudWrapper;
import dev.lone.itemsadder.api.FontImages.PlayerHudsHolderWrapper;
import dev.lone.itemsadder.api.FontImages.PlayerQuantityHudWrapper;
import me.legofreak107.vehiclesplus.VehiclesPlus;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.api.objects.spawn.SpawnMode;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.Part;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.Seat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TimeTrialHolder {
    private final Player player;
    private final Race race;
    private final RaceCuboidStorage storage;
    private final BaseVehicle vehicle;
    private final SpawnedVehicle spawnedVehicle;
    private final Location oldLocation;
    private RivalObject rival;
    private Seat seat;
    private final Timer timer;
    private boolean isRunning;
    private final TimeTrialLap timeTrialLap;
    private final String prefix = DefaultMessages.PREFIX;
    private boolean isInvalidated;
    private boolean isHotLapStart;
    public TimeTrialHolder(Player player, Race race, BaseVehicle vehicle) {
        this.player = player;
        this.race = race;
        this.isHotLapStart = true;
        this.storage = race.getStorage();
        this.vehicle = vehicle;
        this.timer = new Timer(player.getName() + "_timetrial_timer");
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
            if(part instanceof Seat seat) {
                this.seat = seat;
            }
        }
        if(seat != null) {
            start();
        }
    }

    public void start() {
        if (isRunning) {
            return;
        }
        this.player.teleport(storage.getTimeTrialSpawn());
        this.seat.enter(player);
        this.isRunning = true;
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkSectors();
                sendHotbar();
            }
        }, 0, 1);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer != this.player) {
                hide(onlinePlayer);
            }
        }
    }

    public void sendHotbar() {
        if(!isRunning) {
            return;
        }
        if(spawnedVehicle != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Speed: " + spawnedVehicle.getCurrentSpeedInKm()));
        }
    }
    public void stop() {
        if(!isRunning) {
            return;
        }
        timer.cancel();
        player.teleport(oldLocation);
        spawnedVehicle.getStorageVehicle().removeVehicle(this.player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(F1MC.getInstance(), this.player);
        }
        isRunning = false;
        //wrapper.setVisible(false);
    }
    private void checkSectors() {
        Location location = spawnedVehicle.getHolder().getLocation();
        Cuboid s1 = storage.getS1().getCuboid();
        Cuboid s2 = storage.getS2().getCuboid();
        Cuboid s3 = storage.getS3().getCuboid();
        boolean hasRival = rival != null && rival.getLap() != null;
        if(!isInvalidated) {
            for (NamedCuboid cuboid : storage.getLimits().values()) {
                if (cuboid.getCuboid().containsLocation(location)) {
                    isInvalidated = true;
                    player.sendMessage(prefix + ChatColor.RED + "You've invalidated your lap.");
                }
            }
        }
        if (s1.containsLocation(location)) {
            if (!timeTrialLap.isPassedS1()) {
                timeTrialLap.setPassedS1(true);
                timeTrialLap.setPassedS3(false);
                timeTrialLap.setS1L(System.currentTimeMillis());
                long diff = hasRival ? rival.getLap().getS1().getSectorLength()-timeTrialLap.getS1().getSectorLength() : 0;
                if (!isInvalidated) {
                    player.sendMessage(prefix + ChatColor.GRAY + "Your S1 is " + ChatColor.RESET + Utils.millisToTimeString(timeTrialLap.getS1().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? ChatColor.RED : ChatColor.GREEN + "-") + Utils.millisToTimeString(diff) : ""));
                } else {
                    player.sendMessage(prefix + ChatColor.RED + "Your S1 is " + Utils.millisToTimeString(timeTrialLap.getS1().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? "" : "-") + Utils.millisToTimeString(diff) : ""));
                    player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid");
                }
                return;
            }
        }
        if (s2.containsLocation(location)) {
            if (timeTrialLap.isPassedS1() && !timeTrialLap.isPassedS2()) {
                timeTrialLap.setPassedS2(true);
                timeTrialLap.setS2L(System.currentTimeMillis());
                long diff = hasRival ? rival.getLap().getS2().getSectorLength()-timeTrialLap.getS2().getSectorLength() : 0;
                if (!isInvalidated) {
                    player.sendMessage(prefix + ChatColor.GRAY + "Your S2 is " + ChatColor.RESET + Utils.millisToTimeString(timeTrialLap.getS2().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? ChatColor.RED : ChatColor.GREEN + "-") + Utils.millisToTimeString(diff) : ""));
                } else {
                    player.sendMessage(prefix + ChatColor.RED + "Your S2 is " + Utils.millisToTimeString(timeTrialLap.getS2().getSectorLength()) + (hasRival ? " | " + (diff < 0 ? "" : "-") + Utils.millisToTimeString(diff) : ""));
                    player.sendMessage(prefix + ChatColor.RED + "Your lap is invalid");
                }
                return;
            }
        }
        if (s3.containsLocation(location)) {
            if (timeTrialLap.isPassedS1() && timeTrialLap.isPassedS2() && !timeTrialLap.isPassedS3()) {
                timeTrialLap.setPassedS3(true);
                timeTrialLap.setS3L(System.currentTimeMillis());
                if(!isHotLapStart) {
                    timeTrialLap.setLapL(timeTrialLap.getS1().getSectorLength() + timeTrialLap.getS2().getSectorLength() + timeTrialLap.getS3().getSectorLength());
                    long diff = hasRival ? rival.getLap().getS3().getSectorLength()-timeTrialLap.getS3().getSectorLength() : 0;
                    long diffLap = hasRival ? rival.getLap().getLapData().getSectorLength()-timeTrialLap.getLapData().getSectorLength() : 0;
                    if (!isInvalidated) {
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
            }
        }
     }

    private void dataBaseCheck() {
        if(!player.hasPermission(vehicle.getPermissions().getRidePermission())) player.sendMessage(prefix + "You don't have the permissions for this car.");

        MysqlDataSource dataSource = Utils.getDatabase();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `player_uuid` = \""+ player.getUniqueId() +"\" AND `vehicle_name` = \""+ vehicle.getPermissions().getRidePermission() + "\" AND `track_name` = \"" + race.getName() + "\";");
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
                PreparedStatement nextStmt = connection.prepareStatement("INSERT INTO timetrial_laps (`player_uuid`, `lap_length`, `s1_length`, `s2_length`, `s3_length`, `track_name`, `vehicle_name`) VALUES ('"+ player.getUniqueId() +"', "+ timeTrialLap.getLapData().getSectorLength() + ","+ timeTrialLap.getS1().getSectorLength() + ","+ timeTrialLap.getS2().getSectorLength() + ","+ timeTrialLap.getS3().getSectorLength() +",'" + race.getName() + "', '"+ vehicle.getPermissions().getRidePermission() + "');");
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
