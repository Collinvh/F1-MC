package collinvht.f1mc.module.buildingtools.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;

import java.util.UUID;

public class CircuitBuilder {
    @Getter
    private final UUID uuid;
    @Getter @Setter
    private Block prevBlock;
    public CircuitBuilder(UUID uuid) {
        this.uuid = uuid;
    }
}
