package collinvht.projectr.util;

import lombok.Getter;

public enum Permissions {
    FIA_ADMIN("projectr.admin"),
    FIA_RACE("projectr.fia.race");


    @Getter
    private final String permission;
    Permissions(String permissionString) {
        this.permission = permissionString;
    }
}
