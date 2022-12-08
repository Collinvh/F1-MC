package collinvht.projectr.commands.racing.setup.gui;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.racing.setup.SetupManager;
import collinvht.projectr.commands.racing.setup.obj.LimitedFloat;
import collinvht.projectr.commands.racing.setup.obj.LimitedInteger;
import collinvht.projectr.commands.racing.setup.obj.SetupOBJ;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SetupPC {
    public static String preTitle = ChatColor.GRAY + "Geef je auto een setup.";
    public static String aero = ChatColor.GRAY + "Aerodynamic.";
    public static String tyre = ChatColor.GRAY + "Tyres.";
    public static String brake = ChatColor.GRAY + "Brakes.";

    private static final ArrayList<UUID> players = new ArrayList<>();
    private static final HashMap<UUID, String> message = new HashMap<>();

    public static void openFirst(HumanEntity player) {
        Inventory prePc = Bukkit.createInventory(null, 27, preTitle);

        for(int i = 0; i<27; i++) {
            if(i == 11) {
                prePc.setItem(i, createItem("Aero", Material.PHANTOM_MEMBRANE));
            } else if(i == 13) {
                prePc.setItem(i, createItem("Tyre", Material.COAL_ORE));
            } else if(i == 15) {
                prePc.setItem(i, createItem("Brakes", Material.COAL_BLOCK));
            } else {
                prePc.setItem(i, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(prePc);
    }

    public static void openAero(HumanEntity player) {
        Inventory prePc = Bukkit.createInventory(null, 27, aero);
        SetupOBJ obj = SetupManager.getSetup(player.getUniqueId());

        for(int i = 0; i<27; i++) {
            if(i == 11) {
                prePc.setItem(i, createInformativeStack("Front Wing Angle", Material.BLUE_STAINED_GLASS, obj.getFrontWingAngle()));
            } else if(i == 12) {
                prePc.setItem(i, createInformativeStack("Rear Wing Angle", Material.RED_STAINED_GLASS, obj.getRearWingAngle()));
            } else if(i == 14) {
                prePc.setItem(i, createInformativeStack("Front Ride Height", Material.GREEN_STAINED_GLASS, obj.getFrontRideHeight()));
            } else if(i == 15) {
                prePc.setItem(i, createInformativeStack("Rear Ride Height", Material.GREEN_STAINED_GLASS, obj.getRearRideHeight()));
            } else if(i == 22) {
                prePc.setItem(i, createItem("Ga terug.", Material.SPRUCE_SIGN));
            } else {
                prePc.setItem(i, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        player.openInventory(prePc);
    }

    public static void openTyre(HumanEntity player) {
        Inventory prePc = Bukkit.createInventory(null, 27, tyre);
        SetupOBJ obj = SetupManager.getSetup(player.getUniqueId());

        for(int i = 0; i<27; i++) {
            if(i == 11) {
                prePc.setItem(i, createInformativeStack("Front Camber", Material.BLUE_STAINED_GLASS, obj.getFrontCamber()));
            } else if(i == 12) {
                prePc.setItem(i, createInformativeStack("Rear Camber", Material.RED_STAINED_GLASS, obj.getRearCamber()));
            } else if(i == 14) {
                prePc.setItem(i, createInformativeStack("Front Toe", Material.GREEN_STAINED_GLASS, obj.getFrontToe()));
            } else if(i == 15) {
                prePc.setItem(i, createInformativeStack("Rear Toe", Material.GREEN_STAINED_GLASS, obj.getRearToe()));
            } else if(i == 22) {
                prePc.setItem(i, createItem("Ga terug.", Material.SPRUCE_SIGN));
            } else {
                prePc.setItem(i, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        player.openInventory(prePc);
    }

    public static void openBrake(HumanEntity player) {
        Inventory prePc = Bukkit.createInventory(null, 27, brake);
        SetupOBJ obj = SetupManager.getSetup(player.getUniqueId());

        for(int i = 0; i<27; i++) {
            if(i == 11) {
                prePc.setItem(i, createInformativeStack("Brake Pressure", Material.BLUE_STAINED_GLASS, obj.getBrakePressure()));
            } else if(i == 15) {
                prePc.setItem(i, createInformativeStack("Brake Bias", Material.GREEN_STAINED_GLASS, obj.getBrakeBias()));
            } else if(i == 22) {
                prePc.setItem(i, createItem("Ga terug.", Material.SPRUCE_SIGN));
            } else {
                prePc.setItem(i, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        player.openInventory(prePc);
    }


    public static ItemStack createItem(String name, Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }

    public static ItemStack createInformativeStack(String name, Material material, LimitedInteger obj) {
        ItemStack item = createItem(name, material);
        ArrayList<String> info = new ArrayList<>();
        info.add(ChatColor.RESET + "Current : " + obj.getInteger());
        info.add(ChatColor.RESET + "Limits : " + obj.getBottomLimit() + "/" + obj.getTopLimit());
        ItemMeta meta = item.getItemMeta();
        meta.setLore(info);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createInformativeStack(String name, Material material, LimitedFloat obj) {
        ItemStack item = createItem(name, material);
        ArrayList<String> info = new ArrayList<>();
        info.add(ChatColor.RESET + "Current : " + obj.getAFloat());
        info.add(ChatColor.RESET + "Limits : " + obj.getBottomLimit() + "/" + obj.getTopLimit());
        ItemMeta meta = item.getItemMeta();
        meta.setLore(info);
        item.setItemMeta(meta);

        return item;
    }

    public static void runEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(preTitle)) {
            if(event.getCurrentItem() != null) {
                HumanEntity player = event.getView().getPlayer();
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Aero")) {
                    openAero(player);
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Tyre")) {
                    openTyre(player);
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Brakes")) {
                    openBrake(player);
                }
            }

            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(brake)) {
            if(event.getCurrentItem() != null) {
                HumanEntity player = event.getView().getPlayer();

                SetupOBJ obj = SetupManager.getSetup(player.getUniqueId());

                if(obj != null) {
                    if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Brake Pressure")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de brake pressure hebben?");
                        runBrakes(player, obj.getBrakePressure(), "Brake Pressure");

                        players.add(player.getUniqueId());
                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Brake Bias")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de brake bias hebben?");
                        runBrakes(player, obj.getBrakeBias(), "Brake Bias");

                        players.add(player.getUniqueId());
                    }
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Ga terug.")) {
                    openFirst(player);
                }
            }

            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(tyre)) {
            if(event.getCurrentItem() != null) {
                HumanEntity player = event.getView().getPlayer();

                SetupOBJ obj = SetupManager.getSetup(player.getUniqueId());

                if(obj != null) {
                    if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Front Camber")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de front camber hebben?");
                        runTyre(player, obj.getFrontCamber(), "Front Camber");

                        players.add(player.getUniqueId());
                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Rear Camber")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de rear camber hebben?");
                        runTyre(player, obj.getRearCamber(), "Rear Camber");

                        players.add(player.getUniqueId());
                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Front Toe")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de front toe hebben?");
                        runTyre(player, obj.getFrontToe(), "Front Toe");

                        players.add(player.getUniqueId());
                    }  else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Rear Toe")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de rear toe hebben?");
                        runTyre(player, obj.getRearToe(), "Rear Toe");

                        players.add(player.getUniqueId());
                    }
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Ga terug.")) {
                    openFirst(player);
                }
            }

            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(aero)) {
            if(event.getCurrentItem() != null) {
                HumanEntity player = event.getView().getPlayer();

                SetupOBJ obj = SetupManager.getSetup(player.getUniqueId());

                if(obj != null) {
                    if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Front Wing Angle")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de front wing angle hebben?");
                        runAero(player, obj.getFrontWingAngle(), "Front Wing Angle");

                        players.add(player.getUniqueId());
                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Rear Wing Angle")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de rear wing angle hebben?");
                        runAero(player, obj.getRearWingAngle(), "Rear Wing Angle");

                        players.add(player.getUniqueId());
                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Front Ride Height")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de front ride height hebben?");
                        runAero(player, obj.getFrontRideHeight(), "Front Ride Height");

                        players.add(player.getUniqueId());
                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Rear Ride Height")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de rear ride height hebben?");
                        runAero(player, obj.getRearRideHeight(), "Rear Ride Height");

                        players.add(player.getUniqueId());
                    }
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Ga terug.")) {
                    openFirst(player);
                }
            }

            event.setCancelled(true);
        }
    }


    public static void chatEvent(AsyncPlayerChatEvent event) {
        if(players.contains(event.getPlayer().getUniqueId())) {
            message.put(event.getPlayer().getUniqueId(), event.getMessage());
            event.setCancelled(true);
        }
    }

    private static void runTyre(HumanEntity player, LimitedFloat limitedFloat, String info) {
        new BukkitRunnable() {
            final HumanEntity entity = player;
            @Override
            public void run() {
                if(entity != null) {
                    if (message.containsKey(entity.getUniqueId())) {
                        try {
                            float message = Float.parseFloat(SetupPC.message.get(entity.getUniqueId()));
                            SetupPC.message.remove(entity.getUniqueId());
                            SetupPC.players.remove(entity.getUniqueId());

                            limitedFloat.setValue(message);

                            entity.sendMessage(info + " aangepast!");

                            SetupPC.openTyre(entity);

                            Bukkit.getScheduler().cancelTask(this.getTaskId());
                        } catch (NumberFormatException e) {
                            entity.sendMessage("Dat is geen geldig getal...");
                            SetupPC.message.remove(entity.getUniqueId());
                        }
                    }
                } else {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimer(ProjectR.getRacing(), 10, 1);
    }

    private static void runBrakes(HumanEntity player, LimitedInteger limitedFloat, String info) {
        new BukkitRunnable() {
            final HumanEntity entity = player;
            @Override
            public void run() {
                if(entity != null) {
                    if (message.containsKey(entity.getUniqueId())) {
                        try {
                            int message = Integer.parseInt(SetupPC.message.get(entity.getUniqueId()));
                            SetupPC.message.remove(entity.getUniqueId());
                            SetupPC.players.remove(entity.getUniqueId());

                            limitedFloat.setValue(message);

                            entity.sendMessage(info + " aangepast!");

                            SetupPC.openBrake(entity);

                            Bukkit.getScheduler().cancelTask(this.getTaskId());
                        } catch (NumberFormatException e) {
                            entity.sendMessage("Dat is geen geldig getal...");
                            SetupPC.message.remove(entity.getUniqueId());
                        }
                    }
                } else {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimer(ProjectR.getRacing(), 10, 1);
    }

    private static void runAero(HumanEntity player, LimitedInteger limitedFloat, String info) {
        new BukkitRunnable() {
            final HumanEntity entity = player;
            @Override
            public void run() {
                if(entity != null) {
                    if (message.containsKey(entity.getUniqueId())) {
                        try {
                            int message = Integer.parseInt(SetupPC.message.get(entity.getUniqueId()));
                            SetupPC.message.remove(entity.getUniqueId());
                            SetupPC.players.remove(entity.getUniqueId());

                            limitedFloat.setValue(message);

                            entity.sendMessage(info + " aangepast!");

                            SetupPC.openAero(entity);

                            Bukkit.getScheduler().cancelTask(this.getTaskId());
                        } catch (NumberFormatException e) {
                            entity.sendMessage("Dat is geen geldig getal...");
                            SetupPC.message.remove(entity.getUniqueId());
                        }
                    }
                } else {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimer(ProjectR.getRacing(), 10, 1);
    }
}
