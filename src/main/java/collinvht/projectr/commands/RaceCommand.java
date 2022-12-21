package collinvht.projectr.commands;

import collinvht.projectr.ProjectR;
import collinvht.projectr.manager.RacingManager;
import collinvht.projectr.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RaceCommand implements CommandExecutor {
    private static final String prefix = ProjectR.getPluginPrefix();
    private static final RacingManager racing = RacingManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission(Permissions.FIA_ADMIN.getPermission()) || sender.hasPermission(Permissions.FIA_RACE.getPermission())) {
            if(args.length > 0) {
                switch (args[0]) {
                    case "start": {
                        if(args.length > 2) {
                            sender.sendMessage(prefix + racing.startRace(args[1].toLowerCase(), args[2]));
                        } else {
                            sender.sendMessage(prefix + "/race start [name] [mode]");
                        }
                        return true;
                    }
                    case "stop": {
                        if(args.length > 1) {
                            sender.sendMessage(prefix + racing.stopRace(args[1].toLowerCase()));
                        } else {
                            sender.sendMessage(prefix + "/race stop [name]");
                        }
                        return true;
                    }
                    case "delete": {
                        if(args.length > 1) {
                            sender.sendMessage(prefix + racing.deleteRace(args[1].toLowerCase()));
                        } else {
                            sender.sendMessage(prefix + "/race delete [name]");
                        }
                        return true;
                    }
                    case "result": {
                        sender.sendMessage(prefix + racing.getRaceResult());
                        return true;
                    }
                    case "create": {
                        if (args.length > 2) {
                            sender.sendMessage(prefix + racing.createRace(args[1].toLowerCase(), args[2]));
                        } else {
                            sender.sendMessage(prefix + "/race create [name] [laps]");
                        }
                        return true;
                    }
                    case "list": {
                        sender.sendMessage(prefix + racing.listRaces());
                        return true;
                    }
                    case "set": {
                        if (args.length > 3) {
                            if(sender instanceof Player) {
                                sender.sendMessage(prefix + racing.updateRace((Player) sender, args[1].toLowerCase(), args[2], args[3]));
                            } else {
                                sender.sendMessage(prefix + "Dit kun je alleen als speler doen.");
                            }
                        } else {
                            sender.sendMessage(prefix + "/race set [name] [type] [input]");
                        }
                        return true;
                    }
                    default:
                        sender.sendMessage(prefix + "Dit is geen geldig argument.");
                        break;
                }
            } else {
                sender.sendMessage(prefix + "Lorem Ipsum");
            }
        } else {
            sender.sendMessage(prefix + "Je hebt geen toegang tot dit command.");
        }
        return false;
    }
}
