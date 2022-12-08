package collinvht.projectr.listener.driver.object;

import lombok.Getter;

public class FinishData {
    @Getter
    private final int finishPosition;
    @Getter
    private final DriverObject driver;

    public FinishData(DriverObject driver, int finishPosition) {
        this.driver = driver;
        this.finishPosition = finishPosition;
    }
}
