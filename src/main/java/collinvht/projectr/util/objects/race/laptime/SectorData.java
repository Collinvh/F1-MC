package collinvht.projectr.util.objects.race.laptime;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.UUID;

public class SectorData {
    @Getter
    private final UUID driver;

    @Getter @Setter
    private ChatColor sectorColor = ChatColor.RESET;

    @Getter
    private long sectorLength;

    @Setter
    private long sectorStart;

    public SectorData(UUID driver) {
        this.driver = driver;
    }

    public void setSectorLength(long sectorLength) {
        this.sectorLength = (sectorLength - sectorStart);
    }

    public SectorData clone() {
        SectorData data = new SectorData(driver);
        data.sectorLength = sectorLength;
        data.sectorColor = sectorColor;
        return data;
    }
}
