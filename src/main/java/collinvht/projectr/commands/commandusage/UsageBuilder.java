package collinvht.projectr.commands.commandusage;

import collinvht.projectr.util.Permissions;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

public class UsageBuilder {
    private final HashMap<String, Permissions> usages;

    public UsageBuilder() {
        this.usages = new HashMap<>();
    }

    public void addUsage(String usageString) {
        addUsage(usageString, Permissions.NONE);
    }
    public void addUsage(String usageString, Permissions... permissions) {
        for (Permissions permission : permissions) {
            usages.put(usageString, permission);
        }
    }

    public String buildUsages(CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        ArrayList<String> usageStrings = new ArrayList<>();
        usages.forEach((s, permissions) -> {
            if(usageStrings.contains(s)) return;
            if(permissions == Permissions.NONE || permissions.hasPermission(sender)) {
                builder.append(s).append("\n");
                usageStrings.add(s);
            }
        });

        return builder.toString();
    }
}
