package collinvht.zenticracing.commands.racing.laptime.object;

import collinvht.zenticracing.listener.driver.object.DriverObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

public class SectorData {
    @Getter
    private final DriverObject driver;

    @Getter @Setter
    private ChatColor sectorColor = ChatColor.RESET;

    @Getter @Setter
    private long sectorLength;

    public SectorData(DriverObject driver) {
        this.driver = driver;
    }


    public SectorData clone() {
        SectorData data = new SectorData(driver);
        data.sectorLength = sectorLength;
        data.sectorColor = sectorColor;
        return data;
    }
}
