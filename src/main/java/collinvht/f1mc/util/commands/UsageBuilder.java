package collinvht.f1mc.util.commands;

import collinvht.f1mc.util.Permissions;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class UsageBuilder {
    private final HashMap<String, ArrayList<Permissions.Permission>> usages = new HashMap<>();

    /**
     * @author Collinvht
     * Add a usage to {@link #usages}
     */
    public void addUsage(String usageString, Permissions.Permission... permissions) {
        if(permissions.length > 0) {
            for (Permissions.Permission permission : permissions) {
                if(usages.containsKey(usageString)) {
                    usages.get(usageString).add(permission);
                } else {
                    usages.put(usageString, new ArrayList<>(Collections.singleton(permission)));
                }
            }
        } else {
            if(usages.containsKey(usageString)) {
                usages.get(usageString).add(Permissions.NONE);
            } else {
                usages.put(usageString, new ArrayList<>(Collections.singleton(Permissions.NONE)));
            }
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
            boolean hasPermission = true;
            if(permissions.contains(Permissions.NONE)) {
                builder.append(s).append("\n");
                usageStrings.add(s);
            } else {
                for (Permissions.Permission permission : permissions) {
                    hasPermission = permission.isInvert() != permission.hasPermission(sender);
                }
            }
            if(hasPermission) {
                builder.append(s).append("\n");
                usageStrings.add(s);
            }
        });

        return builder.toString();
    }
}
