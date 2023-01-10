package collinvht.projectr.inventory;

import collinvht.projectr.ProjectR;
import collinvht.projectr.manager.race.SetupManager;
import collinvht.projectr.util.objects.LimitedObject;
import collinvht.projectr.util.objects.Setup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SetupInventory extends InventoryBase {
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
                prePc.setItem(i, createItem("Aero", Material.FEATHER));
            } else if(i == 13) {
                prePc.setItem(i, createItem("Tyre", Material.PAPER, 10008));
            } else if(i == 15) {
                prePc.setItem(i, createItem("Brakes", Material.PAPER, 10008));
            } else if(i == 22) {
                prePc.setItem(i, createItem("Ga terug.", Material.SPRUCE_SIGN));
            } else {
                prePc.setItem(i, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(prePc);
    }

    public static void openAero(HumanEntity player) {
        Inventory prePc = Bukkit.createInventory(null, 27, aero);
        Setup obj = SetupManager.getSetup(player.getUniqueId());

        for(int i = 0; i<27; i++) {
            if(i == 10) {
                prePc.setItem(i, createInformativeStack("Front Wing Angle", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
            } else if(i == 11) {
                prePc.setItem(i, createInformativeStack("Rear Wing Angle", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
            } else if(i == 13) {
                prePc.setItem(i, createInformativeStack("Downforce", Material.PHANTOM_MEMBRANE, obj.getDownForceLevel()));
            } else if(i == 15) {
                prePc.setItem(i, createInformativeStack("Front Ride Height", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
            } else if(i == 16) {
                prePc.setItem(i, createInformativeStack("Rear Ride Height", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
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
        Setup obj = SetupManager.getSetup(player.getUniqueId());

        for(int i = 0; i<27; i++) {
            if(i == 11) {
                prePc.setItem(i, createInformativeStack("Front Camber", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
            } else if(i == 12) {
                prePc.setItem(i, createInformativeStack("Rear Camber", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
            } else if(i == 14) {
                prePc.setItem(i, createInformativeStack("Front Toe", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
            } else if(i == 15) {
                prePc.setItem(i, createInformativeStack("Rear Toe", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
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
        Setup obj = SetupManager.getSetup(player.getUniqueId());

        for(int i = 0; i<27; i++) {
            if(i == 11) {
                prePc.setItem(i, createInformativeStack("Brake Pressure", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
            } else if(i == 15) {
                prePc.setItem(i, createInformativeStack("Brake Bias", Material.PAPER, new LimitedObject<>(1.0F, 1.0F), 10008));
            } else if(i == 22) {
                prePc.setItem(i, createItem("Ga terug.", Material.SPRUCE_SIGN));
            } else {
                prePc.setItem(i, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        player.openInventory(prePc);
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
                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Ga terug.")) {
                    PCInventory.openFirst(player);
                }
            }

            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(brake)) {
            if(event.getCurrentItem() != null) {
                HumanEntity player = event.getView().getPlayer();

                Setup obj = SetupManager.getSetup(player.getUniqueId());

                if(obj != null) {
//                    if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Brake Pressure")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de brake pressure hebben?");
//                        runBrakes(player, obj.getBrakePressure(), "Brake Pressure");
//
//                        players.add(player.getUniqueId());
//                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Brake Bias")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de brake bias hebben?");
//                        runBrakes(player, obj.getBrakeBias(), "Brake Bias");
//
//                        players.add(player.getUniqueId());
//                    }
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

                Setup obj = SetupManager.getSetup(player.getUniqueId());

                if(obj != null) {
//                    if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Front Camber")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de front camber hebben?");
//                        runTyre(player, obj.getFrontCamber(), "Front Camber");
//
//                        players.add(player.getUniqueId());
//                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Rear Camber")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de rear camber hebben?");
//                        runTyre(player, obj.getRearCamber(), "Rear Camber");
//
//                        players.add(player.getUniqueId());
//                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Front Toe")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de front toe hebben?");
//                        runTyre(player, obj.getFrontToe(), "Front Toe");
//
//                        players.add(player.getUniqueId());
//                    }  else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Rear Toe")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de rear toe hebben?");
//                        runTyre(player, obj.getRearToe(), "Rear Toe");
//
//                        players.add(player.getUniqueId());
//                    }
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

                Setup obj = SetupManager.getSetup(player.getUniqueId());

                if(obj != null) {
//                    if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Front Wing Angle")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de front wing angle hebben?");
//                        runAero(player, obj.getFrontWingAngle(), "Front Wing Angle");
//
//                        players.add(player.getUniqueId());
//                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Rear Wing Angle")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de rear wing angle hebben?");
//                        runAero(player, obj.getRearWingAngle(), "Rear Wing Angle");
//
//                        players.add(player.getUniqueId());
//                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Front Ride Height")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de front ride height hebben?");
//                        runAero(player, obj.getFrontRideHeight(), "Front Ride Height");
//
//                        players.add(player.getUniqueId());
//                    } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Rear Ride Height")) {
//                        player.closeInventory();
//                        player.sendMessage("Waarop wil jij de rear ride height hebben?");
//                        runAero(player, obj.getRearRideHeight(), "Rear Ride Height");
//
//                        players.add(player.getUniqueId());
//                    }
                    if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Downforce")) {
                        player.closeInventory();
                        player.sendMessage("Waarop wil jij de downforce hebben?");
                        runAero(player, obj.getDownForceLevel(), "Downforce");

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

    private static void runTyre(HumanEntity player, LimitedObject<Float> limitedFloat, String info) {
        new BukkitRunnable() {
            final HumanEntity entity = player;
            @Override
            public void run() {
                if(entity != null) {
                    if (message.containsKey(entity.getUniqueId())) {
                        try {
                            double message = Double.parseDouble(SetupInventory.message.get(entity.getUniqueId()));
                            SetupInventory.message.remove(entity.getUniqueId());
                            SetupInventory.players.remove(entity.getUniqueId());

                            limitedFloat.setValue((float) message);

                            entity.sendMessage(info + " aangepast!");

                            SetupInventory.openTyre(entity);

                            Bukkit.getScheduler().cancelTask(this.getTaskId());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            entity.sendMessage("Dat is geen geldig getal...");
                            SetupInventory.message.remove(entity.getUniqueId());
                        }
                    }
                } else {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimer(ProjectR.getInstance(), 10, 1);
    }

    private static void runBrakes(HumanEntity player, LimitedObject limitedFloat, String info) {
        new BukkitRunnable() {
            final HumanEntity entity = player;
            @Override
            public void run() {
                if(entity != null) {
                    if (message.containsKey(entity.getUniqueId())) {
                        try {
                            double message = Double.parseDouble(SetupInventory.message.get(entity.getUniqueId()));
                            SetupInventory.message.remove(entity.getUniqueId());
                            SetupInventory.players.remove(entity.getUniqueId());

                            limitedFloat.setValue(message);

                            entity.sendMessage(info + " aangepast!");

                            SetupInventory.openBrake(entity);

                            Bukkit.getScheduler().cancelTask(this.getTaskId());
                        } catch (NumberFormatException e) {
                            entity.sendMessage("Dat is geen geldig getal...");
                            SetupInventory.message.remove(entity.getUniqueId());
                        }
                    }
                } else {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimer(ProjectR.getInstance(), 10, 1);
    }

    private static void runAero(HumanEntity player, LimitedObject<Float> limitedFloat, String info) {
        new BukkitRunnable() {
            final HumanEntity entity = player;
            @Override
            public void run() {
                if(entity != null) {
                    if (message.containsKey(entity.getUniqueId())) {
                        try {
                            double message = Double.parseDouble(SetupInventory.message.get(entity.getUniqueId()));
                            SetupInventory.message.remove(entity.getUniqueId());
                            SetupInventory.players.remove(entity.getUniqueId());

                            limitedFloat.setValue((float) message);

                            entity.sendMessage(info + " aangepast!");

                            SetupInventory.openAero(entity);

                            Bukkit.getScheduler().cancelTask(this.getTaskId());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            entity.sendMessage("Dat is geen geldig getal...");
                            SetupInventory.message.remove(entity.getUniqueId());
                        }
                    }
                } else {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimer(ProjectR.getInstance(), 10, 1);
    }
}
