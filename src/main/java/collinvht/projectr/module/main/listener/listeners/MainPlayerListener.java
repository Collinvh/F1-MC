package collinvht.projectr.module.main.listener.listeners;

import collinvht.projectr.module.vehiclesplus.listener.listeners.VPListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class MainPlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void playerQuitEvent(@NotNull PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        VPListener.getRACE_DRIVERS().remove(player.getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void playerKickEvent(@NotNull PlayerKickEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        VPListener.getRACE_DRIVERS().remove(player.getUniqueId());
    }
}
