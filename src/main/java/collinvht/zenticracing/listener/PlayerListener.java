package collinvht.zenticracing.listener;

import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.commands.racing.computer.ers.ERSComputer;
import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

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
    public static void inventoryListener(InventoryClickEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(ERSComputer.preTitle)) {
            if(event.getCurrentItem() != null) {
                if(event.getCurrentItem().getItemMeta() != null) {
                    String name = event.getCurrentItem().getItemMeta().getDisplayName();
                    Player player = Bukkit.getPlayer(name);
                    if(player != null) {
                        DriverObject object = DriverManager.getDriver(player.getUniqueId());
                        ERSComputer.openRace(object.getPlayer(), object.getVehicle());
                    }
                }
            }
            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(ERSComputer.title)) {
            if(event.getCurrentItem() != null) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if(meta != null) {
                    Player player = Bukkit.getPlayer(event.getClickedInventory().getItem(0).getItemMeta().getDisplayName());
                    DriverObject object = DriverManager.getDriver(player.getUniqueId());
                    if(object != null) {
                        RaceCar car = object.getVehicle();
                        if(car != null) {
                            if (meta.getDisplayName().contains("ERS")) {
                                if (meta.getDisplayName().contains("OFF")) {
                                    car.getStorage().setERSMODE(0);
                                } else if (meta.getDisplayName().contains("REGULAR")) {
                                    car.getStorage().setERSMODE(1);
                                } else if(meta.getDisplayName().contains("PUSH")) {
                                    car.getStorage().setERSMODE(2);
                                }
                            } else if (meta.getDisplayName().contains("FM")) {
                                if (meta.getDisplayName().contains("LOW")) {
                                    car.getStorage().setFMMode(0);
                                } else if (meta.getDisplayName().contains("REGULAR")) {
                                    car.getStorage().setFMMode(1);
                                } else if(meta.getDisplayName().contains("PUSH")) {
                                    car.getStorage().setFMMode(2);
                                }
                            }
                        }
                    }
                }
            }

            event.setCancelled(true);
        }
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

    private static boolean equals(InventoryClickEvent event, String text) {
        return event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(text);
    }
}

