package collinvht.f1mc.module.buildingtools.listener.listeners;

import collinvht.f1mc.module.buildingtools.command.commands.BuildingTools;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public static void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(BuildingTools.getPlayers().contains(player.getUniqueId())) {
            Material material = event.getBlock().getType();
            switch (material) {
                case WAXED_EXPOSED_CUT_COPPER_STAIRS: {
                    event.setCancelled(true);
                    Location below = event.getBlock().getLocation().clone().add(0, -1, 0);
                    BlockData data = event.getBlock().getBlockData();

                    Block block = below.getBlock();
                    block.setType(Material.WAXED_EXPOSED_CUT_COPPER_STAIRS, true);
                    block.setBlockData(data);
                    block.getState().update(true);
                }
            }
        }
    }
}
