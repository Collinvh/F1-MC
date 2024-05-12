package collinvht.f1mc.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

@Getter
public class Permissions {
    public static Permission NONE = new Permission("");
    public static Permission BUILDER = new Permission("f1mc.builder");
    public static Permission FIA_ADMIN = new Permission("f1mc.admin");
    public static Permission FIA_RACE = new Permission("f1mc.fia.race");
    public static Permission FIA_TEAM = new Permission("f1mc.fia.team");
    public static Permission FIA_COMMON = new Permission("f1mc.fia.common");


    public static class Permission {
        private final String permission;
        @Getter
        private boolean invert;

        public Permission(String permissionString) {
            this.permission = permissionString;
        }

        public Permission invertPerms() {
            Permission newPerm = new Permission(permission);
            newPerm.invert = true;
            return newPerm;
        }

        public boolean hasPermission(CommandSender sender) {
            if(invert) return !sender.hasPermission(permission);
            return sender.hasPermission(permission);
        }
    }
}
