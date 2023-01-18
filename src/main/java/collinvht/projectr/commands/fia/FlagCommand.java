package collinvht.projectr.commands.fia;

import collinvht.projectr.listener.MTListener;
import collinvht.projectr.manager.race.FlagManager;
import collinvht.projectr.manager.race.RacingManager;
import collinvht.projectr.util.enums.FlagType;
import collinvht.projectr.util.enums.Permissions;
import collinvht.projectr.util.objects.LimitedObject;
import collinvht.projectr.util.objects.commands.CommandUtil;
import collinvht.projectr.util.objects.race.Race;
import collinvht.projectr.util.objects.race.RaceDriver;
import collinvht.projectr.util.objects.race.RaceListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import scala.Int;

public class FlagCommand extends CommandUtil {

    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("stop", 0, "/flag stop", ((sender, command, label, args) -> {
            if(!(sender instanceof Player)) return "Only a player can do this.";
            return FlagManager.stopEditing(((Player) sender).getUniqueId());
        }));
        addPart("edit", 2, "/flag edit [race] [sector]", ((sender, command, label, args) -> {
            if(!(sender instanceof Player)) return "Only a player can do this.";
            Race race = RacingManager.getInstance().getRace(args[1]);
            if(race == null) return "Race doesn't exist.";
            int sector;
            try {
                sector = Integer.parseInt(args[2]);
            } catch (Exception e) {
                return "Invalid number";
            }
            if(sector > 3 || sector < 1) return "Invalid number";
            FlagManager.editFlagLocations(race, sector, ((Player) sender).getUniqueId());
            return "Started editing, to stop use /flag stop";
        }), Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
        addPart("set", 1, "/flag set [type] {sector}", ((sender, command, label, args) -> {
            if (RaceListener.getInstance().isListeningToAnRace()) {
                if (args.length > 2) {
                    FlagType type = FlagType.fromString(args[1]);
                    if (type == null) return "Invalid flag type";
                    int sector;
                    try {
                        sector = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        return "Invalid number";
                    }
                    if(sector > 3 || sector < 1) return "Invalid number";
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (type == FlagType.SC || type == FlagType.VSC) {
                            FlagManager.setAll(RaceListener.getInstance().getCurrentRace(), type);
                            onlinePlayer.sendMessage(prefix + type.getChatColor() + type.getName() + " deployed, reduce speed to " + type.getMaxSpeed() + "km/h.");
                        } else if (type == FlagType.GREEN) {
                            FlagManager.set(RaceListener.getInstance().getCurrentRace(), type, sector);
                            onlinePlayer.sendMessage(prefix + type.getChatColor() + type.getName() + " flag in sector "+ sector +". You can continue to race!");
                        } else {
                            boolean stopSession = type.isStopsSession();
                            StringBuilder builder = new StringBuilder();
                            builder.append(prefix).append(type.getChatColor()).append(type.getName()).append(" flag");
                            if(!stopSession) {
                                builder.append(" in sector ").append(sector);
                                FlagManager.set(RaceListener.getInstance().getCurrentRace(), type, sector);
                            }
                            builder.append(", reduce speed to ").append(type.getMaxSpeed()).append("km/h");
                            if(stopSession) {
                                builder.append("\nAfter that return to the pit-lane.");
                                FlagManager.setAll(RaceListener.getInstance().getCurrentRace(), type);
                            }
                            onlinePlayer.sendMessage(builder.toString());
                        }
                    }
                } else {
                    FlagType type = FlagType.fromString(args[1]);
                    if (type == null) return "Invalid flag type";
                    FlagManager.setAll(RaceListener.getInstance().getCurrentRace(), type);
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (type == FlagType.SC || type == FlagType.VSC) {
                            onlinePlayer.sendMessage(prefix + type.getChatColor() + type.getName() + " deployed, reduce speed to " + type.getMaxSpeed() + "km/h.");
                        } else if (type == FlagType.GREEN) {
                            onlinePlayer.sendMessage(prefix + type.getChatColor() + type.getName() + " flag. You can continue to race!");
                        } else {
                            onlinePlayer.sendMessage(prefix + type.getChatColor() + type.getName() + " flag, reduce speed to " + type.getMaxSpeed() + "km/h.");
                            if (type.isStopsSession()) {
                                onlinePlayer.sendMessage(type.getChatColor() + "After that return into the pit-lane.");
                            }
                        }
                    }
                }
                return "Flag updated";
            } else {
                return "Theirs no on going race.";
            }
        }), Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
    }
}
