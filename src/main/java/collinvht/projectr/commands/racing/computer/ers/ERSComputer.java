package collinvht.projectr.commands.racing.computer.ers;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.racing.computer.RaceCar;
import collinvht.projectr.commands.team.Team;
import collinvht.projectr.commands.team.object.TeamObject;
import collinvht.projectr.listener.driver.DriverManager;
import collinvht.projectr.listener.driver.object.DriverObject;
import collinvht.projectr.manager.tyre.TyreData;
import collinvht.projectr.manager.tyre.TyreManager;
import collinvht.projectr.manager.tyre.Tyres;
import collinvht.projectr.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ERSComputer {
    public static String title = ChatColor.GREEN + "RacePC";
    public static String preTitle = ChatColor.GRAY + "Kies je driver";
    public static String prefix = ProjectR.getServerPrefix();

    private static final HashMap<UUID, Integer> runnables = new HashMap<>();

    public static void stopPlayer(Player playerl) {
        Bukkit.getScheduler().cancelTask(runnables.get(playerl.getUniqueId()));
    }

    public static void openInventory(Player player, TeamObject teamObject) {
        if(teamObject != null) {
            ArrayList<RaceCar> raceCars = teamObject.getRaceCars();

            if(raceCars.size() > 0) {
                Inventory prepc = Bukkit.createInventory(null, 27, preTitle);

                ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta m = item.getItemMeta();
                m.setDisplayName(" ");
                item.setItemMeta(m);
                for(int i=0; i<27; i++) {
                    prepc.setItem(i, item.clone());
                }

                AtomicInteger count = new AtomicInteger();
                raceCars.forEach(car -> {
                    if(count.get() <= 6) {
                        if(car.getDriverObject() != null) {
                            prepc.setItem(10 + count.get(), createPlayer(car.getDriverObject().getPlayer()));
                            count.getAndIncrement();
                        }
                    }
                });

                player.openInventory(prepc);

                DebugUtil.debugMessage("ERS PC geopend voor : " + player.getName());
            } else {
                player.sendMessage(prefix + "Er rijd niemand voor je team!");
            }
        } else {
            player.sendMessage(prefix + "Jij zit niet in een team?");
        }
    }

    public static void openRace(HumanEntity player, RaceCar car, Player player1) {
        Inventory racePC = Bukkit.createInventory(null, 45, title);

        runnables.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                for(int size = 0; size<45; size++) {
                    if (size == 11) {
                        racePC.setItem(size, createItem(ChatColor.WHITE + "ERS OFF", Material.WHITE_CONCRETE));
                    } else if (size == 13) {
                        racePC.setItem(size, createItem(ChatColor.YELLOW + "ERS REGULAR", Material.YELLOW_CONCRETE));
                    } else if (size == 15) {
                        racePC.setItem(size, createItem(ChatColor.RED + "ERS PUSH", Material.RED_CONCRETE));
                    } else if (size == 18) {
                        racePC.setItem(size, createPlayer(player1, car));
                    } else if (size == 29) {
                        racePC.setItem(size, createItem(ChatColor.WHITE + "FM LOW", Material.WHITE_SHULKER_BOX));
                    } else if (size == 31) {
                        racePC.setItem(size, createItem(ChatColor.YELLOW + "FM REGULAR", Material.YELLOW_SHULKER_BOX));
                    } else if (size == 33) {
                        racePC.setItem(size, createItem(ChatColor.RED + "FM PUSH", Material.RED_SHULKER_BOX));
                    } else if (size == 44) {
                        racePC.setItem(size, createItem(ChatColor.GRAY + "Ga terug.", Material.SPRUCE_SIGN));
                    } else {
                        racePC.setItem(size, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
                    }
                }
            }
        }.runTaskTimer(ProjectR.getRacing(), 0, 10).getTaskId());

        player.openInventory(racePC);
    }

    public static ItemStack createItem(String name, Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }

    public static ItemStack createPlayer(Player player) {
        ItemStack pane = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getDisplayName());
        pane.setItemMeta(meta);
        return pane;
    }

    public static ItemStack createPlayer(Player player, RaceCar raceCar) {
        ItemStack pane = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getDisplayName());

        List<String> lore = new ArrayList<>();

        int percentage = (int) (((float) raceCar.getStorage().getErsCount()/100)*100);


        lore.add(ChatColor.YELLOW + "ERS : " + (percentage) + "%");
        lore.add(ChatColor.RED + "Fuel : " + raceCar.getSpawnedVehicle().getStorageVehicle().getVehicleStats().getCurrentFuel().intValue() + "L");

        ItemStack stack = raceCar.getBandGui().getItem(13);
        if(stack != null) {
            TyreData tyre = TyreManager.getDataFromTyre(stack);
            if (tyre.getTyre() != Tyres.NULLTYRE) {
                double dura = tyre.getDurability();
                int perc = (int) ((dura / tyre.getTyre().getData().getDura()) * 100);
                lore.add(ChatColor.GREEN + "Band : " + tyre.getTyre().getName() + " " + " | " + perc +" %");
            }
        }

        meta.setLore(lore);



        pane.setItemMeta(meta);
        return pane;
    }

    public static void runEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(ERSComputer.preTitle)) {
            if(event.getCurrentItem() != null) {
                if(event.getCurrentItem().getItemMeta() != null) {
                    if (event.getCurrentItem().getItemMeta() instanceof SkullMeta) {
                        UUID name = (Objects.requireNonNull(((SkullMeta) event.getCurrentItem().getItemMeta()).getOwningPlayer()).getUniqueId());
                        Player player = Bukkit.getPlayer(name);
                        if (player != null) {
                            DriverObject object = DriverManager.getDriver(player.getUniqueId());
                            ERSComputer.openRace(event.getView().getPlayer(), object.getVehicle(), player);
                        }
                    }
                }
            }
            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(ERSComputer.title)) {
            if(event.getCurrentItem() != null) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                ItemStack stack = event.getView().getItem(18);
                if(meta != null && stack != null) {
                    if (stack.getItemMeta() instanceof SkullMeta) {
                        Player player = Bukkit.getPlayer(((SkullMeta) stack.getItemMeta()).getOwningPlayer().getUniqueId());
                        DriverObject object = DriverManager.getDriver(player.getUniqueId());
                        if (object != null) {
                            RaceCar car = object.getVehicle();
                            if (car != null) {
                                if (meta.getDisplayName().contains("ERS")) {
                                    if (meta.getDisplayName().contains("OFF")) {
                                        car.getStorage().setERSMODE(0);
                                    } else if (meta.getDisplayName().contains("REGULAR")) {
                                        car.getStorage().setERSMODE(1);
                                    } else if (meta.getDisplayName().contains("PUSH")) {
                                        car.getStorage().setERSMODE(2);
                                    }
                                } else if (meta.getDisplayName().contains("FM")) {
                                    if (meta.getDisplayName().contains("LOW")) {
                                        car.getStorage().setFMMode(0);
                                    } else if (meta.getDisplayName().contains("REGULAR")) {
                                        car.getStorage().setFMMode(1);
                                    } else if (meta.getDisplayName().contains("PUSH")) {
                                        car.getStorage().setFMMode(2);
                                    }
                                } else if(meta.getDisplayName().contains("Ga terug.")) {
                                    openInventory((Player) event.getView().getPlayer(), Team.checkTeamForPlayer((Player) event.getView().getPlayer()));
                                }
                            }
                        }
                    }
                }
            }

            event.setCancelled(true);
        }
    }
}
