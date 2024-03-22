package collinvht.f1mc.module.buildingtools.listener.listeners;

import collinvht.f1mc.module.buildingtools.command.commands.BuildingTools;
import collinvht.f1mc.util.DefaultMessages;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemsAdderListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public static void onBlockPlace(CustomBlockPlaceEvent event) {
        ItemStack stack = event.getCustomBlockItem();
        if(stack.getItemMeta() != null) {
            ItemMeta meta = stack.getItemMeta();
            String stripped = ChatColor.stripColor(meta.getDisplayName());
            if(!stripped.contains("Slab") && !stripped.contains("Curbstone")) return;
            Player player = event.getPlayer();
            if(!BuildingTools.getPlayers().containsKey(player.getUniqueId())) {
                player.sendMessage(DefaultMessages.PREFIX + "Can't place curbs outside of building mode");
                event.setCancelled(true);
                return;
            }



            Location location = event.getBlock().getLocation();
            Location above = location.clone().add(0, 1,0);
            World world = location.getWorld();
            if(world == null) return;

            CustomBlock block = CustomBlock.getInstance("projectr:" + stripped.toLowerCase().replace(" ", "_"));
            if(block == null) return;

            block.place(above);
            BlockData data;
            if(stripped.contains("Curbstone")) {
                Snow snow = (Snow) Material.SNOW.createBlockData();
                snow.setLayers(2);
                data = snow;
            } else {
                data = Material.PETRIFIED_OAK_SLAB.createBlockData();
            }

            location.getWorld().setBlockData(location, data);
            event.setCancelled(true);
            block.playPlaceSound();
        }
    }

    @EventHandler
    public static void onBlockBreak(CustomBlockBreakEvent event) {
        ItemStack stack = event.getCustomBlockItem();
        if(stack.getItemMeta() != null) {
            ItemMeta meta = stack.getItemMeta();
            if(meta.getDisplayName().contains("Slab") || meta.getDisplayName().contains("Curbstone")) {
                event.getBlock().getWorld().setBlockData(event.getBlock().getLocation().clone().add(0, -1,0), Material.AIR.createBlockData());
            }
        }
    }
}
