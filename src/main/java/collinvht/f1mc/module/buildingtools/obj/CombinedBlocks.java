package collinvht.f1mc.module.buildingtools.obj;

import dev.lone.itemsadder.api.CustomBlock;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

@Getter
public class CombinedBlocks {
    private final CustomBlock customblock;
    private final Material previousBlock;
    private final Location location;

    public CombinedBlocks(CustomBlock customblock, Location location, Material previousBlock) {
        this.customblock = customblock;
        this.previousBlock = previousBlock;
        this.location = location;
    }

    public void breakBlock() {
        this.customblock.remove();
    }
}
