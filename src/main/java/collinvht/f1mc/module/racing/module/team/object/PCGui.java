package collinvht.f1mc.module.racing.module.team.object;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceCar;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PCGui implements Listener {
    public static String prefix = DefaultMessages.PREFIX;
    public static void open(Player player, TeamObj teamObject) {
        if(teamObject != null) {
            Bukkit.getLogger().warning(teamObject.getTeamPrefix());
            ArrayList<RaceCar> raceCars = teamObject.getRaceCars();

            if (!raceCars.isEmpty()) {
                int sizeInPit = 0;
                for (RaceCar raceCar : raceCars) {
                    if (raceCar.getPlayer() != null) {
                        if(raceCar.getRaceCarGUI().isInMini_game()) {
                            raceCar.getRaceCarGUI().openWindow(player);
                            return;
                        }
                    }
                    sizeInPit += 1;
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
                                                                PCGui.openPC(player, skullPlayer, skullCar, teamObject);
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

    private static void openPC(Player player, Player skullPlayer, RaceCar skullCar, TeamObj team) {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# P # # # # # T #",
                        "# A # # # # # C #",
                        "# # # # B # # # #"
                ).addIngredient('#', new SimpleItem(new ItemBuilder(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))))
                .addIngredient('P', new AutoUpdateItem(20, () -> new ItemBuilder(createPlayer(skullPlayer.getUniqueId()))))
                .addIngredient('A', new AutoUpdateItem(20, () -> new ItemBuilder(currentLap(skullCar, skullPlayer))))
                .addIngredient('T', skullCar.getRaceCarGUI().getBandInventory())
                .addIngredient('C', new AutoUpdateItem(20, () -> new ItemBuilder(fastestLap(skullCar, skullPlayer))))
                .addIngredient('B', new SimpleItem(new ItemStack(Material.BIRCH_SIGN))).build();
        Window window = Window.single()
                .setViewer(player)
                .setTitle(ChatColor.of("#767676") + "Tyre Tool")
                .setGui(gui)
                .build();
        window.open();
    }

    private static ItemStack fastestLap(RaceCar skullCar, Player skullPlayer) {
        RaceDriver driver = VPListener.getRACE_DRIVERS().get(skullPlayer.getUniqueId());
        if(driver != null) {
            if (skullCar.getPlayer() != null) {
                if (RaceManager.getDrivingPlayers().containsKey(skullPlayer)) {
                    Race race = RaceManager.getDrivingPlayers().get(skullPlayer);
                    if (race != null) {
                        if (race.getRaceLapStorage() != null) {
                            if (race.getRaceLapStorage().getRaceMode() != null) {
                                ItemStack stack = new ItemStack(Material.PAPER);
                                ItemMeta meta = stack.getItemMeta();
                                if (meta != null) {
                                    if (race.getRaceLapStorage().getRaceMode().isLapped()) {
                                        meta.setDisplayName(ChatColor.GRAY + "Lap progress");
                                        ArrayList<String> strings = new ArrayList<>();
                                        strings.add(driver.getCurrentLap() + "/" + race.getLaps());
                                        meta.setLore(strings);
                                        stack.setItemMeta(meta);
                                    } else {
                                        meta.setDisplayName(ChatColor.GRAY + "Laps driven");
                                        ArrayList<String> strings = new ArrayList<>();
                                        strings.add(String.valueOf(driver.getCurrentLap()));
                                        meta.setLore(strings);
                                        stack.setItemMeta(meta);
                                    }
                                    return stack;
                                }
                            }
                        }
                    }
                }
            }
        }
        return Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE);
    }

    private static ItemStack currentLap(RaceCar skullCar, Player skullPlayer) {
        RaceDriver driver = VPListener.getRACE_DRIVERS().get(skullPlayer.getUniqueId());
        if(driver != null) {
            if (skullCar.getPlayer() != null) {
                if (RaceManager.getDrivingPlayers().containsKey(skullPlayer)) {
                    Race race = RaceManager.getDrivingPlayers().get(skullPlayer);
                    if (race != null) {
                        if (race.getRaceLapStorage() != null) {
                            if (race.getRaceLapStorage().getRaceMode() != null) {
                                if(driver.getLaptimes(race).getFastestLap() != null) {
                                    ItemStack stack = new ItemStack(Material.PAPER);
                                    ItemMeta meta = stack.getItemMeta();
                                    if (meta != null) {
                                        meta.setDisplayName(ChatColor.GRAY + "Fastest lap");
                                        ArrayList<String> strings = new ArrayList<>();
                                        strings.add(Utils.millisToTimeString(driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()));
                                        meta.setLore(strings);
                                        stack.setItemMeta(meta);
                                        return stack;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE);
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
