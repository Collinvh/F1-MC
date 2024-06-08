package collinvht.f1mc.module.racing.object.laptime;

import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@Getter
public class DriverLaptimeStorage {

    private LinkedList<LaptimeStorage> laptimes = new LinkedList<>();

    private int sectors;

    @Setter
    private long bestS1 = 0;
    @Setter
    private long bestS2 = 0;
    @Setter
    private long bestS3 = 0;

    @Setter
    private LaptimeStorage currentLap;

    @Setter
    private Race race;

    @Setter
    private boolean invalidated;

    @Setter
    private LaptimeStorage fastestLap;

    @Setter
    private int invalidCooldown = 3;

    @Setter
    private int invalidFlags = 0;

    @Setter
    private int penalty = 0;

    public DriverLaptimeStorage(Race race) {
        this.race = race;
    }

    public boolean addLaptime(LaptimeStorage laptimeOBJ) {
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
            return true;
        }
        if(laptimes.size() == 15) {
            laptimes.remove(0);
        }
        return false;
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

    @Setter
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
