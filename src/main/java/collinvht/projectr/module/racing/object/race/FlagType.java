package collinvht.projectr.module.racing.object.race;

import jline.internal.Nullable;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum FlagType {
    GREEN("Green",999, false, ChatColor.GREEN, Material.BLACK_WOOL),
    YELLOW("Yellow",100, false, ChatColor.YELLOW, Material.YELLOW_WOOL),
    RED("Red",30.0, true, ChatColor.RED, true, Material.RED_WOOL),
    VSC("VSC",60.0, true, ChatColor.GOLD, Material.YELLOW_TERRACOTTA),
    SC("SC",60.0, true, ChatColor.GOLD, Material.ORANGE_TERRACOTTA);

    @Getter
    private final String name;
    @Getter
    private final double maxSpeed;
    @Getter
    private final boolean fullCourse;
    @Getter
    private final Material colorMaterial;
    @Getter
    private final ChatColor chatColor;
    @Getter
    private final boolean stopsSession;
    FlagType(String name, double maxSpeed, boolean fullCourse, ChatColor color, Material colorMaterial) {
        this.name = name;
        this.maxSpeed = maxSpeed;
        this.fullCourse = fullCourse;
        this.chatColor = color;
        this.colorMaterial = colorMaterial;
        this.stopsSession = false;
    }
    FlagType(String name, double maxSpeed, boolean fullCourse, ChatColor color, boolean stopSession, Material colorMaterial) {
        this.name = name;
        this.maxSpeed = maxSpeed;
        this.fullCourse = fullCourse;
        this.chatColor = color;
        this.colorMaterial = colorMaterial;
        this.stopsSession = stopSession;
    }


    @Nullable
    public static FlagType fromString(String type) {
        for (FlagType value : FlagType.values()) {
            if(value.getName().equalsIgnoreCase(type)) return value;
        }
        return null;
    }
}