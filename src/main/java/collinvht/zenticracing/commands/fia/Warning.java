package collinvht.zenticracing.commands.fia;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.racing.RaceManager;
import collinvht.zenticracing.commands.racing.object.RaceMode;
import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.commands.racing.setup.gui.SetupPC;
import collinvht.zenticracing.util.objs.DiscordUtil;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.acf.bukkit.contexts.OnlinePlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Warning implements CommandUtil {
    private static final String prefix = ChatColor.DARK_RED + "FIA |" + ChatColor.RED + " Warn >> ";
    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;

    @Getter @Setter
    private static HashMap<UUID, Integer> warningCount = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("zentic.fia") && sender instanceof Player) {
            if(args.length > 0) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    if(RaceManager.getRunningRace() != null) {
                        UUID uuid = player.getUniqueId();
                        RaceObject object = RaceManager.getRunningRace();

                        StringBuilder builder = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            builder.append(args[i] + " ");
                        }

                        if (builder.length() != 0) {
                            CommandUtil.sendMessageToServer(prefix + player.getDisplayName() + " | " + builder.toString());

                            int warningCount = 0;
                            if(Warning.warningCount.get(uuid) != null) {
                                warningCount = Warning.warningCount.get(uuid);
                            }
                            warningCount++;

                            RaceMode mode = object.getRunningMode();
                            if(mode != null) {
                                if(mode.getWarningMargin() != -1) {
                                    if (warningCount >= mode.getWarningMargin()) {
                                        sender.sendMessage(prefix + "Speler heeft nu " + warningCount + " warns! Nu kun jij hem een penalty geven.");
                                    }
                                }
                            }

                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle("Waarschuwing", null);
                            embedBuilder.setColor(Color.RED);
                            embedBuilder.setDescription(player.getName());

                            embedBuilder.addField("Reden", builder.toString(), false);
                            embedBuilder.addField("Warn Count", String.valueOf(warningCount), false);

                            embedBuilder.setFooter("ZenticRacing | " + object.getRaceName());

                            Warning.warningCount.put(uuid, warningCount);

                            DiscordUtil.getChannelByID(844159011666526208L).sendMessage(embedBuilder.build()).queue();
                        } else {
                            warningGUI((Player) sender, player);
                            return true;
                        }
                    } else {
                        sender.sendMessage(zentic + "Er is geen sessie bezig.");
                        return true;
                    }
                } else {
                    sender.sendMessage(zentic + "Die speler bestaat niet");
                    return true;
                }
            } else {
                sender.sendMessage(zentic + "Die speler bestaat niet");
                return true;
            }

        } else {
            sender.sendMessage( zentic + "Geen permissie.");
            return true;
        }
        return true;
    }


    private static void warningGUI(Player player, Player warningPlayer) {
        if(player.hasPermission("zentic.fia")) {
            Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.RED + "WarningGUI");


            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName(" ");
            item.setItemMeta(m);
            for(int i=0; i<27; i++) {
                inventory.setItem(i, item);
            }

            inventory.setItem(11, createPlayer(warningPlayer));

            inventory.setItem(13, createItem(ChatColor.GREEN + "Corner Cut", Material.GREEN_CONCRETE));
            inventory.setItem(15, createItem(ChatColor.YELLOW + "Track Limit", Material.YELLOW_CONCRETE));

            player.openInventory(inventory);
        }
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

    public static void runEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "WarningGUI")) {
            if(event.getClickedInventory() != null) {
                ItemStack stack = event.getClickedInventory().getItem(11);
                if (stack != null) {
                    if (stack.getItemMeta() != null) {
                        if (stack.getItemMeta() instanceof SkullMeta) {
                            SkullMeta meta = (SkullMeta) stack.getItemMeta();
                            Player warnedplayer = (Player) meta.getOwningPlayer();
                            Player player = (Player) event.getWhoClicked();

                            if (warnedplayer != null) {
                                Bukkit.getLogger().warning("warned player not null");
                                if (event.getCurrentItem() != null) {
                                    Bukkit.getLogger().warning("curitem not null");
                                    ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
                                    if (itemMeta != null) {
                                        Bukkit.getLogger().warning("curmeta not null");
                                        String name = ChatColor.stripColor(itemMeta.getDisplayName().toLowerCase());

                                        Bukkit.getLogger().warning(name);

                                        int warningCount;
                                        if (Warning.warningCount.get(warnedplayer.getUniqueId()) != null) {
                                            warningCount = Warning.warningCount.get(warnedplayer.getUniqueId());
                                        } else {
                                            warningCount = 0;
                                        }

                                        switch (ChatColor.stripColor(itemMeta.getDisplayName().toLowerCase())) {
                                            case "corner cut":
                                                CommandUtil.sendMessageToServer(prefix + warnedplayer.getDisplayName() + " | " + "Corner Cut");
                                                warningCount += 1;

                                                EmbedBuilder embedBuilder = new EmbedBuilder();
                                                embedBuilder.setTitle("Waarschuwing", null);
                                                embedBuilder.setColor(Color.RED);
                                                embedBuilder.setDescription(warnedplayer.getName());

                                                embedBuilder.addField("Reden", "Corner Cut || GUI", false);
                                                embedBuilder.addField("Warn Count", String.valueOf(warningCount), false);

                                                embedBuilder.setFooter("ZenticRacing | " + RaceManager.getRunningRace().getRaceName());

                                                DiscordUtil.getChannelByID(844159011666526208L).sendMessage(embedBuilder.build()).queue();
                                                break;
                                            case "track limit":
                                                CommandUtil.sendMessageToServer(prefix + warnedplayer.getDisplayName() + " | " + "Track Limits");
                                                warningCount += 1;

                                                embedBuilder = new EmbedBuilder();
                                                embedBuilder.setTitle("Waarschuwing", null);
                                                embedBuilder.setColor(Color.RED);
                                                embedBuilder.setDescription(warnedplayer.getName());

                                                embedBuilder.addField("Reden", "Corner Cut || GUI", false);
                                                embedBuilder.addField("Warn Count", String.valueOf(warningCount), false);

                                                embedBuilder.setFooter("ZenticRacing | " + RaceManager.getRunningRace().getRaceName());

                                                DiscordUtil.getChannelByID(844159011666526208L).sendMessage(embedBuilder.build()).queue();
                                                break;
                                        }

                                        RaceMode mode = RaceManager.getRunningRace().getRunningMode();
                                        if (mode != null) {
                                            if (mode.getWarningMargin() != -1) {
                                                if (warningCount >= mode.getWarningMargin()) {
                                                    player.sendMessage(prefix + "Speler heeft nu " + warningCount + " warns! Nu kun jij hem een penalty geven.");
                                                }
                                            }
                                        }

                                        Warning.warningCount.put(warnedplayer.getUniqueId(), warningCount);
                                    }
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
