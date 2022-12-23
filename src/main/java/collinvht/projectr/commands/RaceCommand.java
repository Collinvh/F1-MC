package collinvht.projectr.commands;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.commandusage.UsageBuilder;
import collinvht.projectr.manager.RacingManager;
import collinvht.projectr.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RaceCommand implements CommandUtil {
    private static final RacingManager racing = RacingManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean hasFIAPerms = Permissions.FIA_ADMIN.hasPermission(sender) || Permissions.FIA_RACE.hasPermission(sender);
        if (args.length > 0) {
            switch (args[0]) {
                case "start": {
                    if(hasFIAPerms) {
                        if (args.length > 2) {
                            sender.sendMessage(prefix + racing.startRace(args[1].toLowerCase(), args[2]));
                        } else {
                            sender.sendMessage(prefix + "/race start [name] [mode]");
                        }
                        return true;
                    }
                    break;
                }
                case "stop": {
                    if(hasFIAPerms) {
                        sender.sendMessage(prefix + racing.stopRace());
                        return true;
                    }
                    break;
                }
                case "delete": {
                    if(hasFIAPerms) {
                        if (args.length > 1) {
                            sender.sendMessage(prefix + racing.deleteRace(args[1].toLowerCase()));
                        } else {
                            sender.sendMessage(prefix + "/race delete [name]");
                        }
                        return true;
                    }
                    break;
                }
                case "get": {
                    if (sender instanceof Player) {
                        if (args.length > 1) {
                            sender.sendMessage(prefix + racing.getRaceResult(args[1].toLowerCase(), ((Player) sender).getUniqueId()));
                        } else {
                            sender.sendMessage(prefix + "/race get [type]");
                        }
                    } else {
                        sender.sendMessage(prefix + "Dit kun je alleen als speler doen");
                    }
                    return true;
                }
                case "create": {
                    if(hasFIAPerms) {
                        if (args.length > 2) {
                            sender.sendMessage(prefix + racing.createRace(args[1].toLowerCase(), args[2]));
                        } else {
                            sender.sendMessage(prefix + "/race create [name] [laps]");
                        }
                        return true;
                    }
                    break;
                }
                case "list": {
                    if(hasFIAPerms) {
                        sender.sendMessage(prefix + racing.listRaces());
                        return true;
                    }
                    break;
                }
                case "set": {
                    if(hasFIAPerms) {
                        if (args.length > 3) {
                            if (sender instanceof Player) {
                                sender.sendMessage(prefix + racing.updateRace((Player) sender, args[1].toLowerCase(), args[2], args[3]));
                            } else {
                                sender.sendMessage(prefix + "Dit kun je alleen als speler doen");
                            }
                        } else {
                            sender.sendMessage(prefix + "/race set [name] [type] [input]");
                        }
                        return true;
                    }
                    break;
                }
                default:
                    sender.sendMessage(prefix + "Dit is geen geldig argument");
                    return true;
            }
        } else {
            UsageBuilder builder = new UsageBuilder();
            builder.addUsage("/race start [name] [mode]", Permissions.FIA_RACE, Permissions.FIA_ADMIN);
            builder.addUsage("/race stop", Permissions.FIA_RACE, Permissions.FIA_ADMIN);
            builder.addUsage("/race delete [name]", Permissions.FIA_RACE, Permissions.FIA_ADMIN);
            builder.addUsage("/race get [type]");
            builder.addUsage("/race create [name] [laps]", Permissions.FIA_RACE, Permissions.FIA_ADMIN);
            builder.addUsage("/race list", Permissions.FIA_RACE, Permissions.FIA_ADMIN);
            builder.addUsage("/race set [name] [type] [input]", Permissions.FIA_RACE, Permissions.FIA_ADMIN);
            sender.sendMessage(prefix + "Command Usage:\n" + builder.buildUsages(sender));
            return true;
        }
        sender.sendMessage(prefix + "Je hebt geen toegang tot dit command");
        return false;
    }
}
