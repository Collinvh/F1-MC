package collinvht.projectr.util.commands;

import collinvht.projectr.util.Permissions;
import collinvht.projectr.util.DefaultMessages;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandPart {

    @Getter
    private final ArrayList<Permissions> permissions;
    @Getter
    private final String partName;
    @Getter
    private final String usage;
    @Getter
    private final int extraArguments;

    private final CallableCommand<String> function;

    public CommandPart(String name, int extraArguments, String usage, CallableCommand<String> function) {
        this.partName = name;
        this.extraArguments = extraArguments;
        this.usage = usage;
        this.permissions = new ArrayList<>();
        this.function = function;
    }

    /**
     * @author Collinvht
     * Tries to execute the callable {@link #function},
     * if it fails it will throw an error.
     */
    public String execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            return function.call(sender, command, label, args);
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultMessages.ERROR;
        }
    }

    /**
     * @author Collinvht
     * Add a permission to the usage
     */
    public void addPermission(Permissions permission) {
        permissions.add(permission);
    }
    /**
     * @author Collinvht
     * Add multiple permissions to the usage
     */
    public void addPermissions(Permissions... permission) {
        for (Permissions permissions1 : permission) {
            addPermission(permissions1);
        }
    }
}
