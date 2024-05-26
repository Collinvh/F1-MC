package collinvht.f1mc.module.buildingtools.obj;

import collinvht.f1mc.F1MC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class MemorizedEdit {
    private final ArrayList<CombinedBlocks> blockHashMap = new ArrayList<>();
    public void undo() {
        new BukkitRunnable() {
            private int currentId;
            private int currentRow;
            @Override
            public void run() {
                for (int i = currentId; i < blockHashMap.size() && currentRow < 300; i++) {
                    CombinedBlocks block = blockHashMap.get(i);
                    Location location = block.getLocation();
                    if(block.getCustomblock().remove()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                location.getBlock().setType(block.getPreviousBlock());
                            }
                        }.runTaskLater(F1MC.getInstance(), 2);
                    }
                    currentId = i;
                    currentRow += 1;
                }
                if(currentRow >= blockHashMap.size()) {
                    cancel();
                }

                currentRow = 0;
                currentId += 1;
            }
        }.runTaskTimer(F1MC.getInstance(),0, 1);
    }

    public void addEdit(CombinedBlocks blocks) {
        blockHashMap.add(blocks);
    }

    public void runEdit() {
        new BukkitRunnable() {
            private int currentId;
            private int currentRow;
            @Override
            public void run() {
                for (int i = currentId; i < blockHashMap.size() && currentRow < 300; i++) {
                    CombinedBlocks block = blockHashMap.get(i);
                    block.getCustomblock().place(block.getLocation());
                    currentId = i;
                    currentRow += 1;
                }
                if(currentRow >= blockHashMap.size()) {
                    cancel();
                    Bukkit.getLogger().warning("is canceled");
                }
                currentRow = 0;
                currentId += 1;
            }
        }.runTaskTimer(F1MC.getInstance(),0, 1);
    }
}
