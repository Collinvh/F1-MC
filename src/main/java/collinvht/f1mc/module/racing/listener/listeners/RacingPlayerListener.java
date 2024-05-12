package collinvht.f1mc.module.racing.listener.listeners;

import collinvht.f1mc.module.main.objects.SharedObject;
import collinvht.f1mc.module.racing.manager.managers.FlagManager;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.util.DefaultMessages;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RacingPlayerListener implements Listener {

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        FlagManager.getEDITING().remove(event.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onBlockPlace(BlockPlaceEvent event) {
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
                event.getPlayer().sendMessage(DefaultMessages.PREFIX + " > You can only use black wool for this\nUse /flag stop to stop editing the flags");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
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
}
