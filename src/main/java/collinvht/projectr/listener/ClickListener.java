package collinvht.projectr.listener;

import collinvht.projectr.ProjectR;
import collinvht.projectr.inventory.PCInventory;
import collinvht.projectr.manager.race.FlagManager;
import collinvht.projectr.util.objects.SharedObject;
import collinvht.projectr.util.objects.race.Race;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class ClickListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public static void blockClickEvent(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            assert block != null;
            if(block.getType() == Material.NETHER_BRICK_STAIRS) {
                PCInventory.openFirst(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void blockPlaceEvent(BlockPlaceEvent event) {
        if(FlagManager.getEDITING().containsKey(event.getPlayer().getUniqueId())) {
            if(event.getBlockPlaced().getBlockData().getMaterial() == Material.BLACK_WOOL) {
                SharedObject<Integer, Race> map = FlagManager.getEDITING().get(event.getPlayer().getUniqueId());
                switch (map.object1) {
                    case 1: {
                        map.object2.getFlags().getS1loc().add(event.getBlockPlaced().getLocation());
                        break;
                    }
                    case 2: {
                        map.object2.getFlags().getS2loc().add(event.getBlockPlaced().getLocation());
                        break;
                    }
                    case 3: {
                        map.object2.getFlags().getS3loc().add(event.getBlockPlaced().getLocation());
                        break;
                    }
                }
            } else {
                event.getPlayer().sendMessage(ProjectR.getPluginPrefix() + " > You can only use black wool for this\nUse /flag stop to stop editing the flags");
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void blockPlaceEvent(BlockBreakEvent event) {
        if(FlagManager.getEDITING().containsKey(event.getPlayer().getUniqueId())) {
            SharedObject<Integer, Race> map = FlagManager.getEDITING().get(event.getPlayer().getUniqueId());
            switch (map.object1) {
                case 1: {
                    if (map.object2.getFlags().getS1loc().remove(event.getBlock().getLocation())) return;
                    break;
                }
                case 2: {
                    if (map.object2.getFlags().getS2loc().remove(event.getBlock().getLocation())) return;
                    break;
                }
                case 3: {
                    if (map.object2.getFlags().getS3loc().remove(event.getBlock().getLocation())) return;
                    break;
                }
            }
            event.setCancelled(true);
        }
    }

    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new ClickListener(), ProjectR.getInstance());
    }
}
