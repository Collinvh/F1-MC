package collinvht.f1mc.module.racing.object.race;

import jline.internal.Nullable;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Getter
public enum FlagType {
    GREEN("Green",999, false, ChatColor.GREEN, Material.BLACK_WOOL),
    YELLOW("Yellow",100, false, ChatColor.YELLOW, Material.YELLOW_WOOL),
    RED("Red",30.0, true, ChatColor.RED, true, Material.RED_WOOL),
    VSC("VSC",60.0, true, ChatColor.GOLD, Material.YELLOW_TERRACOTTA),
    SC("SC",60.0, true, ChatColor.GOLD, Material.ORANGE_TERRACOTTA);

    private final String name;
    private final double maxSpeed;
    private final boolean fullCourse;
    private final Material colorMaterial;
    private final ChatColor chatColor;
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