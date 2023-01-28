package collinvht.projectr.util.commands;

import collinvht.projectr.util.Permissions;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

public class UsageBuilder {
    private final HashMap<String, Permissions> usages = new HashMap<>();

    /**
     * @author Collinvht
     * Add a usage to {@link #usages}
     */
    public void addUsage(String usageString, Permissions... permissions) {
        if(permissions.length > 0) {
            for (Permissions permission : permissions) {
                usages.put(usageString, permission);
            }
        } else {
            usages.put(usageString, Permissions.NONE);
        }
    }

    /**
     * @author Collinvht
     * This checks the {@link #usages} for usages
     * after that it'll check if the sender has the permission for that command.
     */
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
