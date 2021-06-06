package collinvht.zenticracing.commands.racing;

import collinvht.zenticracing.commands.racing.laptime.LaptimeListener;
import collinvht.zenticracing.commands.racing.laptime.object.Laptime;
import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.TeamBaan;
import collinvht.zenticracing.commands.team.object.TeamBaanObject;
import collinvht.zenticracing.commands.team.object.TeamObject;
import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SnelsteCommand implements CommandExecutor {
    public static final String prefix = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(!(args.length > 0)) {
                TeamObject teamObject = Team.checkTeamForPlayer(((Player) sender).getPlayer());

                TeamBaanObject object = null;
                if(teamObject != null) {
                    object = TeamBaan.getTeamBanen().get(teamObject.getTeamName().toLowerCase());
                }
                if(RaceManager.getRunningRace() != null || object != null) {
                    if(object != null) {
                        if(!object.getObject().isRunning() && RaceManager.getRunningRace() == null) {
                            sender.sendMessage(prefix + "Er is momenteel geen sessie bezig.");
                        }
                    }
                    HashMap<UUID, DriverObject> drivers = DriverManager.getDrivers();

                    if(drivers.values().toArray().length > 0) {

                        LinkedHashMap<DriverObject, Long> sectors = new LinkedHashMap<>();

                        drivers.forEach((unused, driver) -> {
                            if (driver.getLapstorage().getBestTime() != null) {
                                sectors.put(driver, driver.getLapstorage().getBestTime().getLaptime());
                            }
                        });

                        LinkedHashMap<DriverObject, Long> treeMap = sortByValueDesc(sectors);

                        if (treeMap.values().toArray().length > 0) {

                            sender.sendMessage(prefix + " Snelste Laps");

                            AtomicInteger pos = new AtomicInteger();
                            treeMap.forEach((driver, aLong) -> {
                                pos.getAndIncrement();
                                if (driver.getLapstorage().getBestTime() != null) {
                                    TextComponent component = new TextComponent();
                                    component.setText(pos.get() + ". " + driver.getPlayer().getDisplayName() + " " + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getLapData().getSectorLength()));
                                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getS1data().getSectorLength()) + " | " + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getS2data().getSectorLength()) + " | " + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getS3data().getSectorLength()))));
                                    sender.spigot().sendMessage(component);
                                }
                            });
                            RaceObject raceOBJ = RaceManager.getRunningRace();

                            if (raceOBJ != null) {
                                LaptimeListener listener = raceOBJ.getListener();
                                sender.sendMessage("\nTheoretical best : " + Laptime.millisToTimeString(listener.getBestS1() + listener.getBestS2() + listener.getBestS3()));
                            }
                            return true;
                        }
                    }
                    sender.sendMessage(prefix + "Er zijn nog geen laps gereden!");
                    return false;
                } else {
                    sender.sendMessage(prefix + "Er is momenteel geen sessie bezig.");
                }
                return true;
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if(player != null) {
                    DriverObject driver = DriverManager.getDrivers().get(player.getUniqueId());
                    if(driver != null) {
                        if (driver.getLapstorage().getBestTime() == null) {
                            sender.sendMessage(prefix + "Die driver heeft nog geen lap gezet!");
                            return false;
                        }
                        sender.sendMessage(prefix + " " + driver.getPlayer().getDisplayName() + " :");
                        sender.sendMessage("Fastest Lap : " + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getLaptime()));
                        sender.sendMessage("S1/S2/S3 : " + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getS1data().getSectorLength()) + "/" + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getS2data().getSectorLength()) + "/" + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getS3data().getSectorLength()));
                        sender.sendMessage(" ");
                        return true;
                    }
                } else {
                    sender.sendMessage(prefix + "Die driver kon niet gevonden worden!");
                }
            }
        }
        return false;
    }

    public static LinkedHashMap<DriverObject, Long> sortByValueDesc(Map<DriverObject, Long> map) {
        List<Map.Entry<DriverObject, Long>> list = new LinkedList(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Collections.reverse(list);

        LinkedHashMap<DriverObject, Long> result = new LinkedHashMap<>();
        for (Map.Entry<DriverObject, Long> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
