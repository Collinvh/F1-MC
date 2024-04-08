package collinvht.f1mc.module.racing.module.fia.command.commands;

import collinvht.f1mc.module.discord.DiscordModule;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.commands.CommandUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DSQCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("remove", 1, "/dsq remove [playername]", ((sender, command, label, args) -> {
            Player dsqdPlayer = Bukkit.getPlayer(args[1]);
            if (dsqdPlayer != null) {
                RaceDriver driver = VPListener.getRACE_DRIVERS().get(dsqdPlayer.getUniqueId());
                if (driver != null) {
                    if(driver.isDisqualified()) {
                        driver.setDisqualified(false);
                        dsqdPlayer.sendMessage(prefix + "Your disqualification has been revoked");
                        if(Utils.isEnableDiscordModule()) {
                            DiscordModule discordModule = DiscordModule.getInstance();
                            if (discordModule.isInitialized()) {
                                JDA jda = discordModule.getJda();
                                TextChannel channel = jda.getTextChannelById(1217628051853021194L);
                                if (channel != null) {
                                    EmbedBuilder builder = new EmbedBuilder();
                                    builder.setColor(Color.YELLOW);
                                    builder.setTitle("DSQ | " + args[0]);
                                    builder.addField("Reason", "DSQ has been revoked", true);
                                    channel.sendMessage(builder.build()).queue();
                                }
                            }
                        }
                        return prefix + "Drivers disqualification has been removed";
                    } else {
                        return prefix + "Driver can't be disqualified using this method\nUse /dsq [playername] [reason] instead";
                    }
                } else {
                    return prefix + "Driver is not driving.";
                }
            }
            return prefix + "Player doesn't exist";
        }));
        addPart("%", 1, "/dsq [playername] [reason]", ((sender, command, label, args) -> {
            Player dsqdPlayer = Bukkit.getPlayer(args[0]);
            if(dsqdPlayer != null) {
                RaceDriver driver = VPListener.getRACE_DRIVERS().get(dsqdPlayer.getUniqueId());
                if (driver != null) {
                    if (driver.isDriving()) {
                        if (driver.isDisqualified()) {
                            return prefix + "Driver is already disqualified\nUse /dsq remove [playername] instead";
                        } else {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                builder.append(args[i]);
                            }
                            for (Player onlinePlayer : getAllPlayers()) {
                                onlinePlayer.sendMessage(ChatColor.GRAY + "DSQ | " + builder + "\n" + args[1]);
                            }
                            dsqdPlayer.sendMessage(prefix + "You've been disqualified.");
                            dsqdPlayer.sendTitle(ChatColor.GRAY + "DSQ", builder.toString(), 2, 10, 2);
                            driver.setDisqualified(true);
                            DiscordModule discordModule = DiscordModule.getInstance();
                            if (discordModule.isInitialized()) {
                                JDA jda = discordModule.getJda();
                                TextChannel channel = jda.getTextChannelById(1217628051853021194L);
                                if (channel != null) {
                                    EmbedBuilder embedBuilder = new EmbedBuilder();
                                    embedBuilder.setColor(Color.YELLOW);
                                    embedBuilder.setTitle("DSQ | " + args[0]);
                                    embedBuilder.addField("Reason", args[1], true);
                                    channel.sendMessage(embedBuilder.build()).queue();
                                }
                            }
                            return prefix + "Driver has been disqualified";
                        }
                    } else {
                        return prefix + "Driver is not driving.";
                    }
                } else {
                    return prefix + "Driver is not driving.";
                }
            }
            return prefix + "Player doesn't exist";
        }));
    }
}
