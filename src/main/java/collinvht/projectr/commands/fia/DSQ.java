package collinvht.projectr.commands.fia;

import collinvht.projectr.commands.CommandUtil;
import collinvht.projectr.commands.racing.RaceManager;
import collinvht.projectr.commands.racing.object.RaceObject;
import collinvht.projectr.listener.driver.DriverManager;
import collinvht.projectr.listener.driver.object.DriverObject;
import collinvht.projectr.util.objs.DiscordUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DSQ implements CommandUtil {
    private static final String prefix = ChatColor.DARK_RED + "FIA |" + ChatColor.DARK_RED + " DSQ >> ";
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("projectr.fia")) {
            if(args.length > 1) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    DriverObject driver = DriverManager.getDriver(player.getUniqueId());
                    if(RaceManager.getRunningRace() != null) {
                        RaceObject object = RaceManager.getRunningRace();
                        StringBuilder builder = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            builder.append(args[i] + " ");
                        }

                        if (builder.length() != 0) {
                            CommandUtil.sendMessageToServer(prefix + player.getDisplayName() + " | " + builder.toString());

                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle("DSQ", null);
                            embedBuilder.setColor(new Color(0, 0, 0));
                            embedBuilder.setDescription(player.getName());

                            embedBuilder.addField("Reden", builder.toString(), false);

                            embedBuilder.setFooter("Project R | " + object.getRaceName());


                            DiscordUtil.getChannelByID(844159011666526208L).sendMessage(embedBuilder.build()).queue();

                            driver.setBlackFlagged(true);
                        } else {
                            sender.sendMessage(serverPrefix + "Je moet wel een reden invoeren.");
                        }
                    } else {
                        sender.sendMessage(serverPrefix + "Er is geen sessie bezig?");
                        return true;
                    }
                } else {
                    sender.sendMessage(serverPrefix + "Die speler bestaat niet");
                    return true;
                }
            } else {
                sender.sendMessage(serverPrefix + "Je moet wel iets invullen?");
                return true;
            }

        } else {
            sender.sendMessage( serverPrefix + "Geen permissie.");
            return true;
        }
        return true;
    }
}
