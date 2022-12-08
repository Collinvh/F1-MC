package collinvht.projectr.listener;

import collinvht.projectr.commands.fia.Warning;
import collinvht.projectr.commands.racing.computer.band.BandGUI;
import collinvht.projectr.commands.racing.computer.ers.ERSComputer;
import collinvht.projectr.commands.racing.setup.SetupManager;
import collinvht.projectr.commands.racing.setup.gui.SetupPC;
import collinvht.projectr.commands.team.Team;
import collinvht.projectr.listener.driver.DriverManager;
import collinvht.projectr.listener.driver.object.DriverObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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


        SetupManager.createSetupForPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public static void clickBlock(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            assert block != null;
            if(block.getType() == Material.NETHER_BRICK_STAIRS) {
                ERSComputer.openInventory(event.getPlayer(), Team.checkTeamForPlayer(event.getPlayer()));
                event.setCancelled(true);
            }
            if(block.getType() == Material.CHAIN) {
                BandGUI.open(event.getPlayer(), Team.checkTeamForPlayer(event.getPlayer()));
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public static void inventoryClose(InventoryCloseEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(ERSComputer.title)) {
            ERSComputer.stopPlayer((Player) event.getPlayer());
        }

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "Banden")) {
            BandGUI.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public static void inventoryListener(InventoryClickEvent event) {
        ERSComputer.runEvent(event);
        BandGUI.runEvent(event);
        SetupPC.runEvent(event);
        Warning.runEvent(event);
    }

    @EventHandler
    public static void inventoryMoveEvent(InventoryMoveItemEvent event) {
        Container container = (Container) event.getSource().getHolder();

        if(container != null) {
            if(container.getCustomName() != null) {
                if (container.getCustomName().equalsIgnoreCase(ERSComputer.preTitle)) {
                    event.setCancelled(true);
                }
            }
        }

        BandGUI.runEventMove(event);
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
        SetupPC.chatEvent(event);
    }

    private static boolean equals(InventoryClickEvent event, String text) {
        return event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(text);
    }
}

