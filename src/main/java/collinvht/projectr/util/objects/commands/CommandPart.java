package collinvht.projectr.util.objects.commands;

import collinvht.projectr.util.enums.Permissions;
import collinvht.projectr.util.interfaces.CallableCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Callable;

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

    public String execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            return function.call(sender, command, label, args);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong executing a commandPart \n Usage is " + usage);
            e.printStackTrace();
            return "Something went wrong, if you think this is not correct please notify staff";
        }
    }
    public void addPermission(Permissions permission) {
        permissions.add(permission);
    }
    public void addPermissions(Permissions... permission) {
        for (Permissions permissions1 : permission) {
            addPermission(permissions1);
        }
    }
}
