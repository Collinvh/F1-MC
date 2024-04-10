package collinvht.f1mc.module.racing.object.laptime;

import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import collinvht.f1mc.util.Utils;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.UUID;

public class DriverLaptimeStorage {

    @Getter
    private LinkedList<LaptimeStorage> laptimes = new LinkedList<>();

    @Getter
    private int sectors;

    @Getter @Setter
    private long bestS1 = 0;
    @Getter @Setter
    private long bestS2 = 0;
    @Getter @Setter
    private long bestS3 = 0;

    @Getter @Setter
    private LaptimeStorage currentLap;

    @Setter
    @Getter
    private Race race;

    @Getter @Setter
    private boolean invalidated;

    @Getter @Setter
    private LaptimeStorage fastestLap;

    public DriverLaptimeStorage(Race race) {
        this.race = race;
    }

    public void addLaptime(LaptimeStorage laptimeOBJ, RaceMode mode) {
        laptimes.add(laptimeOBJ);

        bestS1 = checkSectorTime(bestS1, laptimeOBJ.getS1data().getSectorLength());
        bestS2 = checkSectorTime(bestS2, laptimeOBJ.getS2data().getSectorLength());
        bestS3 = checkSectorTime(bestS3, laptimeOBJ.getS3data().getSectorLength());

        if(fastestLap != null) {
            if (checkLapTime(fastestLap.getLaptime(), laptimeOBJ.getLaptime())) {
                fastestLap = laptimeOBJ;
            }
        } else {
            fastestLap = laptimeOBJ;
        }
        if(mode == RaceMode.TIMETRIAL) {
            MysqlDataSource dataSource = Utils.getDatabase();
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `player_uuid` = \""+ laptimeOBJ.getDriverUUID().toString() +"\" AND `track_name` = \"" + race.getName() + "\";");
                ResultSet rs = stmt.executeQuery();
                String id = null;
                long length = 0;
                if (rs.next()) {
                    id = rs.getString("timetrial_id");
                    length = rs.getLong("lap_length");
                }
                if(id != null) {
                    if(length > laptimeOBJ.getLaptime()) {
                        PreparedStatement nextStmt = connection.prepareStatement("UPDATE timetrial_laps SET `lap_length`=" + laptimeOBJ.getLaptime() + ", `s1_length`=" + laptimeOBJ.getS1data().getSectorLength() + ", `s2_length`=" + laptimeOBJ.getS2data().getSectorLength() + ", `s3_length`=" + laptimeOBJ.getS3data().getSectorLength() +" WHERE `timetrial_id`=" + id + " AND `track_name` = \""+ race.getName() +"\";");
                        nextStmt.execute();
                        race.updateLeaderboard();
                    }
                } else {
                    PreparedStatement nextStmt = connection.prepareStatement("INSERT INTO timetrial_laps (`player_uuid`, `lap_length`, `s1_length`, `s2_length`, `s3_length`, `track_name`) VALUES ('"+ laptimeOBJ.getDriverUUID() +"', "+ laptimeOBJ.getLaptime() + ","+ laptimeOBJ.getS1data().getSectorLength() + ","+ laptimeOBJ.getS2data().getSectorLength() + ","+ laptimeOBJ.getS3data().getSectorLength() +",'" + race.getName() + "');");
                    nextStmt.execute();
                    race.updateLeaderboard();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getLogger().warning("Error adding laptime");
            }
        } else {
            if(laptimes.size() == 10) {
                //laptimes.remove(0);
            }
        }
    }

    public long checkSectorTime(long best, long newTime) {
        if(best != 0) {
            return Math.min(best, newTime);
        } else return newTime;
    }

    public boolean checkLapTime(long best, long newTime) {
        if(best != 0) {
            return best > newTime;
        } else return true;
    }

    public void addSector() {
        sectors += 1;
    }

    public void resetLaptimes() {
        laptimes = new LinkedList<>();
        sectors = 0;
        currentLap = null;
        fastestLap = null;
        bestS1 = 0;
        bestS2 = 0;
        bestS3 = 0;
        invalidated = false;
    }
}
