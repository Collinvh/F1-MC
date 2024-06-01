package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.discord.DiscordModule;
import collinvht.f1mc.module.racing.module.team.manager.TeamManager;
import collinvht.f1mc.module.racing.module.team.object.TeamObj;
import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.module.racing.module.tyres.obj.TyreClickItem;
import collinvht.f1mc.util.Utils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Set;
import java.util.UUID;

public class RaceCarGUI {
    private final RaceCar holder;
    private final Gui bandGui;
    @Getter
    private final VirtualInventory bandInventory;
    private final VirtualInventory minigame_1;
    private final VirtualInventory minigame_2;
    private final VirtualInventory minigame_3;
    private final VirtualInventory minigame_4;
    private Gui minigameGui;
    @Getter
    private boolean isInMini_game;

    private final int runnableID;

    private Player intialOpenedPlayer;

    public RaceCarGUI(RaceCar car) {
        this.holder = car;
        int[] sizes = new int[]{1};
        this.bandInventory = new VirtualInventory(UUID.randomUUID(), 1, null, sizes);
        bandInventory.setPreUpdateHandler(this::ItemPreUpdate);
        ItemStack[] minigameItems = new ItemStack[]{Utils.emptyStack(Material.RED_STAINED_GLASS_PANE)};
        minigame_1 = new VirtualInventory(UUID.randomUUID(), 1, minigameItems.clone(), sizes);
        minigame_2 = new VirtualInventory(UUID.randomUUID(), 1, minigameItems.clone(), sizes);
        minigame_3 = new VirtualInventory(UUID.randomUUID(), 1, minigameItems.clone(), sizes);
        minigame_4 = new VirtualInventory(UUID.randomUUID(), 1, minigameItems.clone(), sizes);
        minigame_1.setPreUpdateHandler(event -> event.setCancelled(true));
        minigame_2.setPreUpdateHandler(event -> event.setCancelled(true));
        minigame_3.setPreUpdateHandler(event -> event.setCancelled(true));
        minigame_4.setPreUpdateHandler(event -> event.setCancelled(true));
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                canContinue();
            }
        };
        runnableID = runnable.runTaskTimer(F1MC.getInstance(), 0, 20).getTaskId();

        this.bandGui = createTyreInventory();
    }

    private Gui createInventory(ItemStack stack) {
        Gui gui = Gui.normal().setStructure(
                        "# # # # # # # # #",
                        "# T ! # # # ! Y #",
                        "# # # # # # # # #",
                        "# L ! # # # ! R #",
                        "# # # # # # # # #"
                ).addIngredient('#', new SimpleItem(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE)))
                .addIngredient('T', new TyreClickItem(this, stack == null ? bandInventory.getItem(0) : stack, minigame_1))
                .addIngredient('Y', new TyreClickItem(this, stack == null ? bandInventory.getItem(0) : stack, minigame_2))
                .addIngredient('L', new TyreClickItem(this, stack == null ? bandInventory.getItem(0) : stack, minigame_3))
                .addIngredient('R', new TyreClickItem(this, stack == null ? bandInventory.getItem(0) : stack, minigame_4))
                .build();
        gui.setSlotElement(11, new SlotElement.InventorySlotElement(minigame_1, 0));
        gui.setSlotElement(15, new SlotElement.InventorySlotElement(minigame_2, 0));
        gui.setSlotElement(29, new SlotElement.InventorySlotElement(minigame_3, 0));
        gui.setSlotElement(33, new SlotElement.InventorySlotElement(minigame_4, 0));
        return gui;
    }
    private Gui createTyreInventory() {
        Gui gui = Gui.normal().setStructure(
                "# # # # # # # # #",
                "# # # # A # # # #",
                "# # # # # # # # #"
        ).addIngredient('#', new SimpleItem(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))).build();
        gui.setSlotElement(13, new SlotElement.InventorySlotElement(bandInventory, 0));

        return gui;
    }
    private boolean isWaitingOnTask;
    public void ItemPreUpdate(ItemPreUpdateEvent event) {
        if(!canContinue()) return;

        if(isWaitingOnTask) {
            Bukkit.getLogger().warning("isWaiting");
            event.setCancelled(true);
            return;
        }
        if(TyreManager.isTyre(event.getNewItem()) && event.getPreviousItem() == null) {
            isWaitingOnTask = true;
            new BukkitRunnable() {
                @Override
                public void run() {
                    startMinigame(false, event.getNewItem());
                    isWaitingOnTask = false;
                }
            }.runTaskLater(F1MC.getInstance(), 1);
            event.setCancelled(false);
        } else if(event.getPreviousItem() != null) {
            Bukkit.getLogger().warning("previtem not null");
            startMinigame(true, event.getNewItem());
            event.setCancelled(true);
        } else {
            Bukkit.getLogger().warning("just cancel it mf");
            event.setCancelled(true);
        }
    }

    public void openWindow(Player player) {
        Window window;
        if(intialOpenedPlayer == null) intialOpenedPlayer = player;
        if(isInMini_game) {
            window = Window.single()
                    .setViewer(player)
                    .setGui(minigameGui)
                    .setTitle(ChatColor.of("#767676") + "Change Tyre")
                    .build();
        } else {
            window = Window.single()
                    .setViewer(player)
                    .setGui(bandGui)
                    .setTitle(ChatColor.of("#767676") + "Change Tyre")
                    .build();
        }
        window.addCloseHandler(new BukkitRunnable() {
            @Override
            public void run() {
                if(player == intialOpenedPlayer) {
                    intialOpenedPlayer = null;
                    if(bandGui != null) {
                        for (Player allCurrentViewer : bandGui.findAllCurrentViewers()) {
                            allCurrentViewer.closeInventory();
                        }
                    }
                    if(minigameGui != null) {
                        for (Player allCurrentViewer : minigameGui.findAllCurrentViewers()) {
                            allCurrentViewer.closeInventory();
                        }
                    }
                }
            }
        });
        window.open();
    }

    private boolean removeTyre;
    private ItemStack newItem;
    private void startMinigame(boolean removeTyre, ItemStack newItem) {
        this.removeTyre = removeTyre;
        this.minigameGui = createInventory(removeTyre ? null : newItem);
        this.isInMini_game = true;
        this.newItem = newItem == null ? new ItemStack(Material.AIR) : newItem;
        minigame_1.forceSetItem(UpdateReason.SUPPRESSED, 0, Utils.emptyStack(Material.RED_STAINED_GLASS_PANE));
        minigame_2.forceSetItem(UpdateReason.SUPPRESSED, 0, Utils.emptyStack(Material.RED_STAINED_GLASS_PANE));
        minigame_3.forceSetItem(UpdateReason.SUPPRESSED, 0, Utils.emptyStack(Material.RED_STAINED_GLASS_PANE));
        minigame_4.forceSetItem(UpdateReason.SUPPRESSED, 0, Utils.emptyStack(Material.RED_STAINED_GLASS_PANE));

        bandGui.findAllCurrentViewers().forEach(player -> {
            Window window = Window.single()
                    .setViewer(player)
                    .setGui(minigameGui)
                    .setTitle(ChatColor.of("#767676") + "Change Tyre")
                    .build();
            window.open();
        });
    }

    public void checkIfComplete() {
        if(!canContinue()) return;
        if(minigame_1.getItem(0).getType() == Material.LIME_STAINED_GLASS_PANE && minigame_2.getItem(0).getType() == Material.LIME_STAINED_GLASS_PANE && minigame_3.getItem(0).getType() == Material.LIME_STAINED_GLASS_PANE && minigame_4.getItem(0).getType() == Material.LIME_STAINED_GLASS_PANE) {
            Set<Player> players = minigameGui.findAllCurrentViewers();
            if(removeTyre) {
                if (!players.isEmpty()) {
                    Player randomPlayer = players.stream().findAny().get();
                    randomPlayer.getInventory().addItem(bandInventory.getItem(0));
                    if(Utils.isEnableDiscordModule()) {
                        DiscordModule discordModule = DiscordModule.getInstance();
                        if (discordModule.isInitialized()) {
                            TeamObj teamObj = TeamManager.getTeamForPlayer(randomPlayer);
                            if(teamObj != null) {
                                JDA jda = discordModule.getJda();
                                TextChannel channel = jda.getTextChannelById(1217628051853021194L);
                                if (channel != null) {
                                    EmbedBuilder builder = new EmbedBuilder();
                                    builder.setColor(teamObj.getTeamColor().getColor());
                                    builder.setTitle("Tyre change | " + teamObj.getTeamName());
                                    builder.addField("Previous Tyre", TyreManager.getTyreName(bandInventory.getItem(0)), true);
                                    builder.addField("New Tyre", TyreManager.getTyreName(newItem), true);
                                    builder.addBlankField(true);
                                    builder.addField("Player", holder.getPlayer().getDriverName(), true);
                                    channel.sendMessage(builder.build()).queue();
                                }
                            }
                        }
                    }
                }
                bandInventory.forceSetItem(UpdateReason.SUPPRESSED,0, newItem);
            } else {
                if(Utils.isEnableDiscordModule()) {
                    if (!players.isEmpty()) {
                        Player randomPlayer = players.stream().findAny().get();
                        DiscordModule discordModule = DiscordModule.getInstance();
                        if (discordModule.isInitialized()) {
                            TeamObj teamObj = TeamManager.getTeamForPlayer(randomPlayer);
                            if (teamObj != null) {
                                JDA jda = discordModule.getJda();
                                TextChannel channel = jda.getTextChannelById(1217628051853021194L);
                                if (channel != null) {
                                    EmbedBuilder builder = new EmbedBuilder();
                                    builder.setColor(teamObj.getTeamColor().getColor());
                                    builder.setTitle("Tyre change | " + teamObj.getTeamName());
                                    builder.addField("New Tyre", TyreManager.getTyreName(newItem), true);
                                    builder.addBlankField(true);
                                    builder.addField("Player", holder.getPlayer().getDriverName(), true);
                                    channel.sendMessage(builder.build()).queue();
                                }
                            }
                        }
                    }
                }
            }
            players.forEach(p -> {
                Window window = Window.single()
                        .setViewer(p)
                        .setGui(bandGui)
                        .setTitle(ChatColor.of("#767676") + "Change Tyre")
                        .build();
                window.open();
            });
            this.isInMini_game = false;
        }
    }

    public NBTItem getTyre() {
        if(isInMini_game) return null;
        if(bandInventory.getItem(0) != null) {
            if(TyreManager.isTyre(bandInventory.getItem(0))) {
                return new NBTItem(bandInventory.getItem(0));
            }
        }
        return null;
    }

    public boolean canContinue() {
        if(holder.getLinkedVehicle() == null) {
            for (Player allCurrentViewer : bandGui.findAllCurrentViewers()) {
                allCurrentViewer.closeInventory();
            }
            return false;
        }
        return true;
    }

    public void stopTimer() {
        Bukkit.getScheduler().cancelTask(runnableID);
    }
}
