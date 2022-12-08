package collinvht.projectr.util.objs;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.racing.RaceManager;
import collinvht.projectr.commands.racing.SnelsteCommand;
import collinvht.projectr.commands.racing.laptime.object.Laptime;
import collinvht.projectr.commands.team.Team;
import collinvht.projectr.commands.team.object.TeamObject;
import collinvht.projectr.listener.driver.DriverManager;
import collinvht.projectr.listener.driver.object.DriverObject;
import collinvht.projectr.manager.tyre.Tyres;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscordUtil {

    @Getter
    private static JDA jda;

    public static String discordPrefix = "~";

    private static final Commands commands = new Commands();

    public static void init(ProjectR racing) {
        try {
            jda = JDABuilder.createDefault("MTA1MDU0MjgzOTA4OTYwNjcyNw.Gs5wOm.POiVmZVe-nOEStsSCQeh-CvGToZm7VXGSLS-Uk").build();
            jda.awaitReady();

            jda.getPresence().setActivity(Activity.streaming("ProjectR", "https://discord.gg/ykXmbNgA7X"));

            jda.addEventListener(commands);

        } catch (LoginException | InterruptedException ignored) {
            Bukkit.getLogger().warning("Error whilst initializing discord hook, disabling this part of the plugin");
        }
    }

    public synchronized static void close() {
        jda.removeEventListener(commands);
        jda.getPresence().setPresence(OnlineStatus.OFFLINE, Activity.streaming("ProjectR", "https://discord.gg/ykXmbNgA7X"));
        jda.shutdownNow();
    }


    public static TextChannel getChannelByID(long id) {
        return jda.getTextChannelById(id);
    }

    public static List<TextChannel> getChannelsByName(String name) {
        return jda.getTextChannelsByName(name, true);
    }

    public static void setPresence(OnlineStatus status, Activity activity) {
        jda.getPresence().setPresence(status, activity);
    }


    static class Commands extends ListenerAdapter {

        public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");

            if(event.getChannel().getIdLong() == 1049746928063226010L) {
                if (args[0].equalsIgnoreCase(discordPrefix + "stand")) {
                    sendStand(event);
                } else if (args[0].equalsIgnoreCase(discordPrefix + "snelste")) {
                    sendSnelste(event);
                }
            }
        }

        private void sendSnelste(@NotNull GuildMessageReceivedEvent event) {
            if(RaceManager.getRunningRace() != null) {
                HashMap<UUID, DriverObject> drivers = DriverManager.getDrivers();

                if(drivers.values().toArray().length > 0) {

                    LinkedHashMap<DriverObject, Long> sectors = new LinkedHashMap<>();

                    drivers.forEach((unused, driver) -> {
                        if (driver.getLapstorage().getBestTime() != null) {
                            sectors.put(driver, driver.getLapstorage().getBestTime().getLaptime());
                        }
                    });

                    LinkedHashMap<DriverObject, Long> treeMap = SnelsteCommand.sortByValueDesc(sectors);

                    if (treeMap.values().toArray().length > 0) {

                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Snelste Laps", null);
                        embedBuilder.setColor(new Color(100, 100, 5));

                        AtomicInteger pos = new AtomicInteger();
                        treeMap.forEach((driver, aLong) -> {
                            pos.getAndIncrement();
                            if (driver.getLapstorage().getBestTime() != null) {
                                String tyre;
                                if(driver.getLapstorage().getBestTime().getTyre() != Tyres.NULLTYRE) {
                                    tyre = " [" + driver.getLapstorage().getBestTime().getTyre().getName().charAt(0) + "]";
                                } else {
                                    tyre = " [?]";
                                }

                                TeamObject team = Team.checkTeamForPlayer(driver.getPlayer());
                                String teamstr = "";
                                if(team != null) {
                                    teamstr = StringUtils.capitalize(team.getTeamName()) + " | ";
                                }

                                embedBuilder.addField(pos.get() + ".",  teamstr + driver.getPlayer().getName() + " " + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getLapData().getSectorLength()) + " " + tyre, false);
                            }
                        });
                        event.getChannel().sendMessage(embedBuilder.build()).queue();
                    } else {
                        event.getChannel().sendMessage("Er zijn nog geen laps gereden!").queue();
                    }
                }
            } else {
                event.getChannel().sendMessage("Er is geen sessie bezig!").queue();
            }
        }

        private void sendStand(@NotNull GuildMessageReceivedEvent event) {
            HashMap<UUID, DriverObject> drivers = DriverManager.getDrivers();
            LinkedHashMap<DriverObject, Integer> sectors = new LinkedHashMap<>();

            drivers.forEach((unused, driver) -> sectors.put(driver, driver.getLapstorage().getSectors()));

            LinkedHashMap<DriverObject, Integer> treeMap = DriverManager.sortByValueDesc(sectors);

            if(treeMap.size() > 0 && RaceManager.getRunningRace() != null) {

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Sector Stand", null);
                embedBuilder.setColor(new Color(100, 100, 0));

                AtomicInteger pos = new AtomicInteger();
                treeMap.forEach((driver, integer) -> {
                    if(integer > 0) {
                        pos.getAndIncrement();
                        TeamObject team = Team.checkTeamForPlayer(driver.getPlayer());
                        String teamstr = "";
                        if(team != null) {
                            teamstr = StringUtils.capitalize(team.getTeamName()) + " | ";
                        }
                        embedBuilder.addField(pos.get() + ".", teamstr + driver.getPlayer().getName() + " : " + integer, false);
                    }
                });

                embedBuilder.setFooter("Project R");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                event.getChannel().sendMessage("Er is nog niemand aan het rijden!").queue();
            }
        }
    }
}
