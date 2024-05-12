package collinvht.f1mc.module.timetrial.object;

import collinvht.f1mc.module.racing.object.laptime.SectorData;
import collinvht.f1mc.util.Utils;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Setter
@Getter
public class TimeTrialLap {
    private SectorData s1;
    private SectorData s2;
    private SectorData s3;
    private SectorData lapData;
    private boolean passedS1;
    private boolean passedS2;
    private boolean passedS3;

    public TimeTrialLap(UUID holder) {
        this.s1 = new SectorData(holder);
        this.s2 = new SectorData(holder);
        this.s3 = new SectorData(holder);
        this.lapData = new SectorData(holder);
    }


    public static TimeTrialLap fromUUID(UUID uuid, String trackName) {
        MysqlDataSource dataSource = Utils.getDatabase();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `player_uuid` = \"" + uuid.toString() + "\" AND `track_name` = \"" + trackName + "\";");
            ResultSet rs = stmt.executeQuery();
            String id = null;
            long total_length = 0;
            long s1_length = 0;
            long s2_length = 0;
            long s3_length = 0;
            if (rs.next()) {
                id = rs.getString("timetrial_id");
                total_length = rs.getLong("lap_length");
                s1_length = rs.getLong("s1_length");
                s2_length = rs.getLong("s2_length");
                s3_length = rs.getLong("s3_length");
            }

            if(id != null && s1_length > 0 && s2_length > 0 && s3_length > 0 && total_length > 0) {
                TimeTrialLap lap = new TimeTrialLap(uuid);
                SectorData s1 = new SectorData(uuid);
                s1.setSectorLength(s1_length);
                SectorData s2 = new SectorData(uuid);
                s2.setSectorLength(s2_length);
                SectorData s3 = new SectorData(uuid);
                s3.setSectorLength(s3_length);
                SectorData total = new SectorData(uuid);
                total.setSectorLength(total_length);
                lap.setS1(s1);
                lap.setS2(s2);
                lap.setS3(s3);
                lap.setLapData(total);
                return lap;
            }
        } catch (SQLException ignored) {}
        return new TimeTrialLap(uuid);
    }

    public void setS1L(long s1) {
        this.s2.setSectorStart(s1);
        this.s1.setSectorLengthL(s1);
    }

    public void setS2L(long s2) {
        this.s3.setSectorStart(s2);
        this.s2.setSectorLengthL(s2);
    }

    public void setS3L(long s3) {
        this.s1.setSectorStart(s3);
        this.s3.setSectorLengthL(s3);
    }

    public void setLapL(long l) {
        this.lapData.setSectorLength(l);
    }
}
