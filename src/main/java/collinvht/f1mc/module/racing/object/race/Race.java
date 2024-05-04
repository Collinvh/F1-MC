package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Utils;
import com.google.gson.JsonObject;
import com.mysql.cj.jdbc.MysqlDataSource;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
public class Race {
    @Setter
    private int laps;

    @Setter
    private String name;

    @Setter
    private RaceCuboidStorage storage;

    private final RaceLapStorage raceLapStorage;

    @Setter
    private RaceFlags flags;

    @Setter
    private boolean timeTrialStatus;

    @Setter
    private Hologram leaderBoard;

    @Getter @Setter
    private RaceTimer raceTimer;


    public Race(String name, int laps) {
        this.laps = laps;
        this.name = name;
        this.storage = new RaceCuboidStorage();
        this.flags = new RaceFlags();
        this.raceLapStorage = new RaceLapStorage(this);
    }

    public void saveJson() {
        File path = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/races/").toFile();

        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("Name", name);
        mainObject.addProperty("Laps", laps);
        mainObject.addProperty("TimeTrial_Status", timeTrialStatus);
        mainObject.add("TimeTrial_Spawn", storage.ttSpawnJson());
        mainObject.add("TimeTrial_Leaderboard", storage.ttLeaderboardJson());
        mainObject.add("Cuboids", storage.toJson());
        mainObject.add("Flags", flags.toJson());

        Utils.saveJSON(path, name, mainObject);
    }

    public static Race createRaceFromJson(JsonObject object) {
        try {
            String name = object.get("Name").getAsString();
            int laps = object.get("Laps").getAsInt();
            boolean ttstatus = object.get("TimeTrial_Status").getAsBoolean();

            RaceCuboidStorage raceStorage = RaceCuboidStorage.fromJson(object.get("Cuboids").getAsJsonObject());
            if(raceStorage != null) {
                raceStorage.setTimeTrialSpawnObj(object.get("TimeTrial_Spawn").getAsJsonObject());
                raceStorage.setTimeTrialLeaderboardObj(object.get("TimeTrial_Leaderboard").getAsJsonObject());
                Race race = new Race(name, laps);
                race.setFlags(RaceFlags.fromJson(object.get("Flags").getAsJsonObject()));
                race.setTimeTrialStatus(ttstatus);
                race.setStorage(raceStorage);
                return race;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    public void updateLeaderboard() {
        if(!Utils.isEnableTimeTrial()) return;
        if (DHAPI.getHologram(name + "_leaderboard") != null) {
            deleteLeaderboard();
        }
        Location location = storage.getTimeTrialLeaderboard();
        if (location.getWorld() != null) {
            leaderBoard = DHAPI.createHologram(name + "_leaderboard", location.clone().add(0, 5.5,0));
            leaderBoard.setDisplayRange(20);
            leaderBoard.setUpdateRange(20);
            leaderBoard.setAlwaysFacePlayer(false);
        }

        if(leaderBoard == null) return;
        MysqlDataSource dataSource = Utils.getDatabase();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `track_name` = '"+ name +"' ORDER BY `lap_length` ASC LIMIT 20;");
            ResultSet rs = stmt.executeQuery();
            DHAPI.addHologramLine(leaderBoard, DefaultMessages.PREFIX + "Fastest laps at " + ChatColor.GRAY + name + ChatColor.RESET + ":");
            int curNumber = 1;
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                String vehicleName = rs.getString("vehicle_name").replace("f1mc.", "");
                String finalVehicleName = (vehicleName.substring(0, 1).toUpperCase() + vehicleName.substring(1)).replace(".", " ").replace("_", " ");
                DHAPI.addHologramLine(leaderBoard, curNumber + ". " + ChatColor.GRAY + finalVehicleName + " " + player.getName() + " : " + ChatColor.RESET + Utils.millisToTimeString(rs.getLong("lap_length")));
                curNumber += 1;
            }
        } catch (SQLException ignored) {}
    }

    public void deleteLeaderboard() {
        DHAPI.removeHologram(name + "_leaderboard");
    }
}
