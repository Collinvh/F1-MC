package collinvht.projectr.listener.driver.object;

import collinvht.projectr.commands.racing.laptime.object.Laptime;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class LaptimeStorage {
    @Getter
    private ArrayList<Laptime> laptimes = new ArrayList<>();

    @Getter @Setter
    private Laptime bestTime;

    @Getter @Setter
    private boolean invalidated;

    @Getter @Setter
    private Laptime currentLap;

    @Getter
    private int sectors = 0;

    @Getter @Setter
    private boolean isPastS1;

    @Getter @Setter
    private boolean isPastS2;

    @Getter @Setter
    private boolean pastPitExit;

    @Getter @Setter
    private long bestS1 = 0;
    @Getter @Setter
    private long bestS2 = 0;
    @Getter @Setter
    private long bestS3 = 0;


    public void addLaptime(Laptime laptimeOBJ) {

        if(laptimes.size() == 10) {
            laptimes.remove(0);
        }
        laptimes.add(laptimeOBJ);

        bestS1 = checkSectorTime(bestS1, laptimeOBJ.getS1());
        bestS2 = checkSectorTime(bestS2, laptimeOBJ.getS2());
        bestS3 = checkSectorTime(bestS3, laptimeOBJ.getS3());

        if(bestTime != null) {
            if (checkLapTime(bestTime.getLaptime(), laptimeOBJ.getLaptime())) {
                bestTime = laptimeOBJ;
            }
        } else {
            bestTime = laptimeOBJ;
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
        laptimes = new ArrayList<>();
    }

}
