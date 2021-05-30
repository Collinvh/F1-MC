package collinvht.zenticracing.listener.driver.object;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

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
