package collinvht.zenticracing.commands.fia;

import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.racing.RaceManager;
import collinvht.zenticracing.commands.racing.object.RaceMode;
import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.util.objs.DiscordUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class Warning implements CommandUtil {
    private static final String prefix = ChatColor.DARK_RED + "FIA |" + ChatColor.RED + " Warn >> ";
    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;

    @Getter @Setter
    private static HashMap<UUID, Integer> warningCount = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("zentic.fia")) {
            if(args.length > 1) {
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
                            sendMessageToServer(prefix + player.getDisplayName() + " | " + builder.toString());

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
                            sender.sendMessage(zentic + "Je moet wel een reden invoeren.");
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
                sender.sendMessage(zentic + "Je moet wel iets invullen?");
                return true;
            }

        } else {
            sender.sendMessage( zentic + "Geen permissie.");
            return true;
        }
        return true;
    }
}
