package collinvht.zenticmain.command;

import collinvht.zenticmain.discord.DiscordManager;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Penalty implements CommandExecutor, CommandUtil {
    private static final String prefix = ChatColor.DARK_RED + "FIA |" + ChatColor.DARK_RED + " Penalty >> ";
    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > ";
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("zentic.fia")) {
            if(args.length > 1) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        builder.append(args[i] + " ");
                    }

                    if (builder.length() != 0) {
                        sendMessageToServer(prefix + player.getDisplayName() + " | " + builder.toString());

                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Penalty", null);
                        embedBuilder.setColor(new Color(127,0,0));
                        embedBuilder.setDescription(player.getName());

                        embedBuilder.addField("Reden", builder.toString(), false);

                        embedBuilder.setFooter("ZenticRacing");


                        DiscordManager.getWarningChannel().sendMessage(embedBuilder.build()).queue();
                    } else {
                        sender.sendMessage(zentic + "Je moet wel een reden invoeren.");
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
