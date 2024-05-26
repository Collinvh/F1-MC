package collinvht.f1mc.module.main.listener.listeners;

import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.Utils;
import com.nametagedit.plugin.api.events.NametagFirstLoadedEvent;
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
        RaceDriver driver = VPListener.getRACE_DRIVERS().get(player.getUniqueId());
        if(driver != null) {
            driver.setVehicle(null);
            driver.setDriving(false);
            driver.setInPit();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void playerJoinEvent(@NotNull NametagFirstLoadedEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if(Utils.isEnableCountryModule()) {
            CountryManager.updatePlayer(player);
        }

        if(RaceManager.getTimingRace() != null) {
            RaceManager.getTimingRace().getRaceTimer().addPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void playerKickEvent(@NotNull PlayerKickEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        RaceDriver driver = VPListener.getRACE_DRIVERS().get(player.getUniqueId());
        if(driver != null) {
            driver.setVehicle(null);
            driver.setDriving(false);
            driver.setInPit();
        }
    }
}
