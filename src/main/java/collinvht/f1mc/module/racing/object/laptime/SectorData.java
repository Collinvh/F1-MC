package collinvht.f1mc.module.racing.object.laptime;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.UUID;

public class SectorData {
    @Getter
    private final UUID driver;

    @Getter @Setter
    private ChatColor sectorColor = ChatColor.RESET;

    @Getter @Setter
    private long sectorLength;

    @Getter @Setter
    private long sectorDifference;

    @Setter @Getter
    private long sectorStart;

    public SectorData(UUID driver) {
        this.driver = driver;
    }

    public void setSectorLengthL(long sectorLength) {
        this.sectorLength = (sectorLength - sectorStart);
    }

    public SectorData copy() {
        SectorData data = new SectorData(driver);
        data.sectorLength = sectorLength;
        data.sectorColor = sectorColor;
        return data;
    }
}
