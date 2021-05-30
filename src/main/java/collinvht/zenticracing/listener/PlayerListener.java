package collinvht.zenticracing.listener;

import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    public PlayerListener() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            DriverObject driver = DriverManager.getDrivers().get(player.getUniqueId());
            if(driver != null) {
                driver.setPlayer(player);
            } else {
                DriverObject obj = new DriverObject(player.getUniqueId());
                obj.setPlayer(player);
                DriverManager.addDriver(obj);
            }
        }
    }

    @EventHandler
    public static void playerJoin(PlayerJoinEvent event) {
        DriverObject driver = DriverManager.getDrivers().get(event.getPlayer().getUniqueId());
        if(driver != null) {
            driver.setPlayer(event.getPlayer());
        } else {
            DriverObject obj = new DriverObject(event.getPlayer().getUniqueId());
            obj.setPlayer(event.getPlayer());
            DriverManager.addDriver(obj);
        }
    }

    @EventHandler
    public static void playerLeave(PlayerQuitEvent event) {
        DriverObject driver = DriverManager.getDrivers().get(event.getPlayer().getUniqueId());
        if(driver != null) {
            driver.setDriving(false);
        }
    }

    @EventHandler
    public static void playerChat(AsyncPlayerChatEvent event) {

    }
}

