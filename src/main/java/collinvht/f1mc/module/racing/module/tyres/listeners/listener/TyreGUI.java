package collinvht.f1mc.module.racing.module.tyres.listeners.listener;

import collinvht.f1mc.module.racing.module.team.object.TeamObj;
import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.module.racing.object.race.RaceCar;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.chat.ChatHexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TyreGUI implements Listener {
    public static String prefix = DefaultMessages.PREFIX;
    public static void open(Player player, TeamObj teamObject) {
        if(teamObject != null) {
            ArrayList<RaceCar> raceCars = teamObject.getRaceCars();

            if (!raceCars.isEmpty()) {
                int sizeInPit = 0;
                for (RaceCar raceCar : raceCars) {
                    if (raceCar.getPlayer() != null) {
                        if(raceCar.getRaceCarGUI().isInMini_game()) {
                            raceCar.getRaceCarGUI().openWindow(player);
                            return;
                        }
                        sizeInPit += raceCar.getPlayer().isInPit() ? 1 : 0;
                    }
                }
                if(sizeInPit > 0) {
                    Gui gui = Gui.normal()
                            .setStructure(
                                    "# # # # # # # # #",
                                    "# # # # # # # # #",
                                    "# # # # # # # # #"
                            ).addIngredient('#', new SimpleItem(new ItemBuilder(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))))
                            .build();

                    AtomicInteger count = new AtomicInteger();
                    raceCars.forEach(car -> {
                        if (count.get() <= 6) {
                            if (car.getPlayer() != null) {
                                gui.setItem(10 + count.get(), new SimpleItem(createPlayer(car.getPlayer().getDriverUUID()), click -> {
                                    InventoryClickEvent event = click.getEvent();
                                    if(event != null) {
                                        if(event.getCurrentItem() != null) {
                                            if (event.getCurrentItem().getItemMeta() instanceof SkullMeta) {
                                                UUID name = (Objects.requireNonNull(((SkullMeta) event.getCurrentItem().getItemMeta()).getOwningPlayer()).getUniqueId());
                                                Player skullPlayer = Bukkit.getPlayer(name);
                                                if (skullPlayer != null) {
                                                    RaceDriver object = VPListener.getRACE_DRIVERS().get(name);
                                                    if (object != null) {
                                                        if (object.getVehicle() != null) {
                                                            RaceCar skullCar = VPListener.getRACE_CARS().get(object.getVehicle().getHolder().getUniqueId());
                                                            if (skullCar != null) {
                                                                if (!object.isInPit()) {
                                                                    event.getWhoClicked().sendMessage(prefix + "Car is not in the pit lane");
                                                                    event.setCancelled(true);
                                                                    return;
                                                                }

                                                                if (object.getVehicle().getCurrentSpeedInKm() <= 1) {
                                                                    skullCar.getRaceCarGUI().openWindow(player);
                                                                } else {
                                                                    event.getWhoClicked().sendMessage(prefix + "Car is moving..");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }));
                                count.getAndIncrement();
                            }
                        }
                    });
                    Window window = Window.single()
                            .setViewer(player)
                            .setTitle(ChatColor.of("#767676") + "Tyre Tool")
                            .setGui(gui)
                            .build();
                    window.open();
                } else {
                    player.sendMessage(prefix + "There is no one in the pits at the moment");
                }
                return;
            }
            player.sendMessage(prefix + "There is no one driving for your team at this moment");
        } else {
            player.sendMessage(prefix + "Your not in a team");
        }
    }

    public static ItemStack createPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        ItemStack pane = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        if(player != null) {
            if (meta != null) {
                meta.setOwningPlayer(player);
                meta.setDisplayName(player.getName());
                pane.setItemMeta(meta);
            }
        }
        return pane;
    }

}