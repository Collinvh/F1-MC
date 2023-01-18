package collinvht.projectr.listener;

import collinvht.projectr.ProjectR;
import collinvht.projectr.inventory.PCInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import collinvht.projectr.inventory.SetupInventory;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

public class InventoryListener implements Listener {

    @EventHandler
    public static void playerChat(AsyncPlayerChatEvent event) {
        SetupInventory.chatEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void inventoryListener(InventoryClickEvent e) {
        SetupInventory.runEvent(e);
        PCInventory.runEvent(e);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void inventoryMoveEvent(InventoryMoveItemEvent event) {
//        if(players.contains(event.getDestination())) {
//
//        }
    }

    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), ProjectR.getInstance());
    }
}
