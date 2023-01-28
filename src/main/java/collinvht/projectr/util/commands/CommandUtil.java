package collinvht.projectr.util.commands;

import collinvht.projectr.util.Permissions;
import collinvht.projectr.util.DefaultMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class CommandUtil implements CommandExecutor {
    private final HashMap<String, CommandPart> parts = new HashMap<>();
    protected static final String prefix = DefaultMessages.PREFIX;


    /**
     * @author Collinvht
     * This is used to after the initialization of the command,
     * this uses {{@link #parts}} to look if a part uses a wildcard "%"
     * otherwise it will check if the argument provided exists and tries to continue
     * to work forward using that part.
     */
    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(parts.isEmpty()) initializeCommand(sender);
        if(args.length > 0) {
            if(parts.containsKey("%")) {
                CommandPart part = parts.get("%");
                if(part.getExtraArguments() > 0) {
                    if(args.length > part.getExtraArguments()) {
                        sender.sendMessage(part.execute(sender, command, label, args));
                        return true;
                    } else {
                        sender.sendMessage(DefaultMessages.PREFIX + DefaultMessages.WRONG_USAGE + part.getUsage());
                        return false;
                    }
                } else {
                    sender.sendMessage(part.execute(sender, command, label, args));
                    return true;
                }

            } else if(parts.containsKey(args[0])) {
                CommandPart part = parts.get(args[0]);
                if(part.getExtraArguments() > 0) {
                    if(args.length > part.getExtraArguments()) {
                        sender.sendMessage(part.execute(sender, command, label, args));
                        return true;
                    } else {
                        sender.sendMessage(DefaultMessages.PREFIX + DefaultMessages.WRONG_USAGE + part.getUsage());
                        return false;
                    }
                } else {
                    sender.sendMessage(part.execute(sender, command, label, args));
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

    /**
     * @author Collinvht
     * Sends the usage of the command to the commandSender
     * this uses {{@link #parts}} to see which part uses which usage
     */
    private void sendUsage(CommandSender sender) {
        UsageBuilder builder = new UsageBuilder();
        parts.forEach((s, commandPart) -> builder.addUsage(commandPart.getUsage(), commandPart.getPermissions().toArray(new Permissions[]{})));
        sender.sendMessage(DefaultMessages.PREFIX + DefaultMessages.COMMAND_USAGE + builder.buildUsages(sender));
    }

    /**
     * @author Collinvht
     * Used to initialize the parts for the command to use.
     */
    protected abstract void initializeCommand(@NotNull CommandSender commandSender);

    /**
     * @author Collinvht
     * Adds a commandPart to {@link #parts}
     */
    protected void addPart(String name, int extraArguments, String usage, CallableCommand<String> function, Permissions... permissions) {
        if(parts.containsKey(name)) return;

        CommandPart part = new CommandPart(name, extraArguments, usage, function);
        if(permissions.length > 0) {
            part.addPermissions(permissions);
        } else {
            part.addPermission(Permissions.NONE);
        }
        parts.put(name, part);
    }
}
