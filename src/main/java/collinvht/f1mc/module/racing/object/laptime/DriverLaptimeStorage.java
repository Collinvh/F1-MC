package collinvht.f1mc.module.racing.object.laptime;

import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
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

    @Getter @Setter
    private int invalidCooldown = 3;

    @Getter @Setter
    private int invalidFlags = 0;

    @Getter @Setter
    private int penalty = 0;

    public DriverLaptimeStorage(Race race) {
        this.race = race;
    }

    public void addLaptime(LaptimeStorage laptimeOBJ, RaceMode mode) {
        laptimes.add(laptimeOBJ);

        bestS1 = checkSectorTime(bestS1, laptimeOBJ.getS1().getSectorLength());
        bestS2 = checkSectorTime(bestS2, laptimeOBJ.getS2().getSectorLength());
        bestS3 = checkSectorTime(bestS3, laptimeOBJ.getS3().getSectorLength());

        if(fastestLap != null) {
            if (checkLapTime(fastestLap.getLapData().getSectorLength(), laptimeOBJ.getLapData().getSectorLength())) {
                fastestLap = laptimeOBJ;
            }
        } else {
            fastestLap = laptimeOBJ;
        }
        if(laptimes.size() == 10) {
            laptimes.remove(0);
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

    @Getter @Setter
    private long lastPassedSectorTime = -1;
    public void addSector() {
        sectors += 1;
        lastPassedSectorTime = System.currentTimeMillis();
    }

    public void resetLaptimes() {
        laptimes = new LinkedList<>();
        sectors = 0;
        fastestLap = null;
        bestS1 = 0;
        bestS2 = 0;
        bestS3 = 0;
        penalty = 0;
        invalidated = false;
    }
}
