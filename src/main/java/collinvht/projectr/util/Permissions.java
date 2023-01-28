package collinvht.projectr.util;

import lombok.Getter;
import org.bukkit.command.CommandSender;

public enum Permissions {
    NONE(""),
    FIA_ADMIN("projectr.admin"),
    FIA_RACE("projectr.fia.race"),
    FIA_TEAM("projectr.fia.team"),
    FIA_COMMON("projectr.fia.common");


    @Getter
    private final String permission;
    Permissions(String permissionString) {
        this.permission = permissionString;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }
}
