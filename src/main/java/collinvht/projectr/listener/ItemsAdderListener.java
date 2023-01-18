package collinvht.projectr.listener;

import collinvht.projectr.ProjectR;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomBlock$Advanced;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemsAdderListener implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    public static void onBlockPlace(CustomBlockPlaceEvent event) {
        ItemStack stack = event.getCustomBlockItem();
        if(stack.getItemMeta() != null) {
            ItemMeta meta = stack.getItemMeta();
            if(meta.getDisplayName().contains("Slab") || meta.getDisplayName().contains("Curbstone")) {
                String stripped = ChatColor.stripColor(meta.getDisplayName());
                Bukkit.getLogger().warning("projectr:" + stripped.toLowerCase().replace(" ", "_"));
                CustomBlock block = CustomBlock.getInstance("projectr:" + stripped.toLowerCase().replace(" ", "_"));
                block.place(event.getBlock().getLocation().clone().add(0, 1,0));
                event.getBlock().getWorld().setBlockData(event.getBlock().getLocation(), Material.PETRIFIED_OAK_SLAB.createBlockData());
                event.setCancelled(true);
            }
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

    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new ItemsAdderListener(), ProjectR.getInstance());
    }
}
