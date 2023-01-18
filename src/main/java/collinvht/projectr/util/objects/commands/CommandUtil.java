package collinvht.projectr.util.objects.commands;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.enums.Permissions;
import collinvht.projectr.util.interfaces.CallableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class CommandUtil implements CommandExecutor {
    /*
    Used for fast accessibility
     */
    protected final String prefix = ProjectR.getPluginPrefix();
    private final HashMap<String, CommandPart> parts = new HashMap<>();


    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(parts.isEmpty()) initializeCommand(sender);
        if(args.length > 0) {
            if(parts.containsKey(args[0])) {
                CommandPart part = parts.get(args[0]);
                if(part.getExtraArguments() > 0) {
                    if(args.length > part.getExtraArguments()) {
                        sender.sendMessage(prefix + part.execute(sender, command, label, args));
                        return true;
                    } else {
                        sender.sendMessage(prefix + "Wrong usage: \n" + part.getUsage());
                        return false;
                    }
                } else {
                    sender.sendMessage(prefix + part.execute(sender, command, label, args));
                    return true;
                }
            } else {
                sendUsage(sender);
                return false;
            }
        } else {
            sendUsage(sender);
            return false;
        }
    }

    private void sendUsage(CommandSender sender) {
        UsageBuilder builder = new UsageBuilder();
        parts.forEach((s, commandPart) -> builder.addUsage(commandPart.getUsage(), commandPart.getPermissions().toArray(new Permissions[]{})));
        sender.sendMessage(prefix + "Command usage \n" + builder.buildUsages(sender));
    }

    protected abstract void initializeCommand(@NotNull CommandSender commandSender);

    protected void addPart(String name, int extraArguments, String usage, CallableCommand<String> function) {
        addPart(name,extraArguments, usage, function, Permissions.NONE);
    }

    protected void addPart(String name, int extraArguments, String usage, CallableCommand<String> function, Permissions... permissions) {
        if(parts.containsKey(name)) return;

        CommandPart part = new CommandPart(name, extraArguments, usage, function);
        part.addPermissions(permissions);
        parts.put(name, part);
    }
}
