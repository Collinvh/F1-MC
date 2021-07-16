package collinvht.zenticracing.listener.driver.object;

import lombok.Getter;
import lombok.Setter;

public class RaceStorage {
    @Getter @Setter
    private int lap;

    @Getter @Setter
    private boolean pastDRSPoint;

    @Getter @Setter
    private boolean finished;

    @Getter @Setter
    private boolean inPit;
}
