package collinvht.projectr.commands.racing.computer.band;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.racing.RaceManager;
import collinvht.projectr.commands.racing.computer.RaceCar;
import collinvht.projectr.commands.team.object.TeamObject;
import collinvht.projectr.listener.driver.DriverManager;
import collinvht.projectr.listener.driver.object.DriverObject;
import collinvht.projectr.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BandGUI {
    public static String prefix = ProjectR.getServerPrefix();
    public static String preTitle = ChatColor.GRAY + "Banden Tool";

    private static final HashMap<UUID, RaceCar> cars = new HashMap<>();

    public static void open(Player player, TeamObject teamObject) {
        if(teamObject != null) {
            ArrayList<RaceCar> raceCars = teamObject.getRaceCars();

            if (raceCars.size() > 0) {
                Inventory prepc = Bukkit.createInventory(null, 27, preTitle);

                ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta m = item.getItemMeta();
                if(m == null) {
                    player.sendMessage(prefix + "Er ging iets fout");
                    return;
                }
                m.setDisplayName(" ");
                item.setItemMeta(m);
                for (int i = 0; i < 27; i++) {
                    prepc.setItem(i, item.clone());
                }

                AtomicInteger count = new AtomicInteger();
                raceCars.forEach(car -> {
                    if (count.get() <= 6) {
                        if (car.getDriverObject() != null) {
                            prepc.setItem(10 + count.get(), createPlayer(car.getDriverObject().getPlayer()));
                            count.getAndIncrement();
                        }
                    }
                });
                player.openInventory(prepc);
                DebugUtil.debugMessage("Banden PC geopend voor : " + player.getName());
            } else {
                player.sendMessage(prefix + "Er rijd niemand voor je team!");
            }
        } else {
            player.sendMessage(prefix + "Jij zit niet in een team?");
        }
    }

    public static ItemStack createPlayer(Player player) {
        ItemStack pane = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        if(meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(player.getDisplayName());
            pane.setItemMeta(meta);
        }
        return pane;
    }


    public static void runEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(BandGUI.preTitle)) {
            if(event.getCurrentItem() != null) {
                if(event.getCurrentItem().getItemMeta() != null) {
                    if (event.getCurrentItem().getItemMeta() instanceof SkullMeta) {
                        UUID name = (Objects.requireNonNull(((SkullMeta) event.getCurrentItem().getItemMeta()).getOwningPlayer()).getUniqueId());
                        Player player = Bukkit.getPlayer(name);
                        if (player != null) {
                            DriverObject object = DriverManager.getDriver(player.getUniqueId());
                            if(object != null) {
                                if(object.getVehicle() != null) {
                                    if(object.getVehicle().getSpawnedVehicle() != null) {
                                        if(RaceManager.getRunningRace() != null) {
                                            if(!object.getRaceStorage().isInPit()) {
                                                event.getWhoClicked().sendMessage(prefix + "De auto moet in de pitlane staan, sorry!..");
                                                event.setCancelled(true);
                                                return;
                                            }
                                        }

                                        if(object.getVehicle().getSpawnedVehicle().getCurrentSpeedInKm() <= 1) {
                                            cars.put(event.getWhoClicked().getUniqueId(), object.getVehicle());
                                            event.getWhoClicked().openInventory(object.getVehicle().getBandGui());
                                        } else {
                                            event.getWhoClicked().sendMessage(prefix + "Auto moet stilstaan..");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "Banden")) {
            if(event.getCurrentItem() != null) {
                if(event.getCurrentItem().getItemMeta() != null) {
                    if(event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public static void runEventMove(InventoryMoveItemEvent event) {
    }

    public static void removePlayer(HumanEntity player) {
        cars.remove(player.getUniqueId());
    }
}
