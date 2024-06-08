package collinvht.f1mc.module.racing.object.laptime;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.UUID;

@Getter
public class SectorData {
    private final UUID driver;

    //Todo: fix deprecated
    @Setter
    private ChatColor sectorColor = ChatColor.RESET;

    @Setter
    private long sectorLength;

    @Setter
    private long sectorDifference;

    @Setter
    private long sectorStart;

    @Setter
    private String sectorName;

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
