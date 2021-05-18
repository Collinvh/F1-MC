package collinvht.zenticmain.event;

import collinvht.zenticmain.ZenticMain;
import me.legofreak107.vehiclesplus.vehicles.api.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class VPPEvents implements Listener {

    private static final ArrayList<VPPPlugin> plugins = new ArrayList<>();


    @EventHandler(priority = EventPriority.HIGHEST)
    public static void VehicleEnterEvent(VehicleEnterEvent event) {
        for (VPPPlugin plugin : plugins) {
            plugin.VehicleEnterEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void VehicleLeaveEvent(VehicleLeaveEvent event) {
        for (VPPPlugin plugin : plugins) {
            plugin.VehicleLeaveEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void VehicleCollisionEvent(VehicleCollisionEvent event) {
        for (VPPPlugin plugin : plugins) {
            plugin.VehicleCollisionEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void VehicleSpawnedEvent(VehicleSpawnedEvent event) {
        for (VPPPlugin plugin : plugins) {
            plugin.VehicleSpawnedEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void VehicleDestroyEvent(VehicleDestroyEvent event) {
        for (VPPPlugin plugin : plugins) {
            plugin.VehicleDestroyEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void VehicleDamageEvent(VehicleDamageEvent event) {
        for (VPPPlugin plugin : plugins) {
            plugin.VehicleDamageEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void VehicleBuyEvent(VehicleBuyEvent event) {
        for (VPPPlugin plugin : plugins) {
            plugin.VehicleBuyEvent(event);
        }
    }


    public static void addPlugin(VPPPlugin plugin) {
        plugins.add(plugin);
        ZenticMain.LogMessage(plugin.getPlName() + " Initialized");
    }
}
