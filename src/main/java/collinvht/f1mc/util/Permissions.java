package collinvht.f1mc.util;

import lombok.Getter;
import org.bukkit.command.CommandSender;

public enum Permissions {
    NONE(""),
    BUILDER("f1mc.builder"),
    FIA_ADMIN("f1mc.admin"),
    FIA_RACE("f1mc.fia.race"),
    FIA_TEAM("f1mc.fia.team"),
    FIA_COMMON("f1mc.fia.common");


    @Getter
    private final String permission;
    Permissions(String permissionString) {
        this.permission = permissionString;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }
}
