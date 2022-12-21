package collinvht.projectr.util.objects.race.laptime;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

public class Laptimes {

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

    @Getter @Setter
    private boolean invalidated;

    @Getter @Setter
    private LaptimeStorage fastestLap;

    public void addLaptime(LaptimeStorage laptimeOBJ) {

        if(laptimes.size() == 10) {
            laptimes.remove(0);
        }
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
    }
}
