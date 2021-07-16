package collinvht.zenticracing.manager.tyre;

import collinvht.zenticracing.util.objs.TyreConfigData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Tyres {
    NULLTYRE(Material.BARRIER, "?????", 0, ChatColor.WHITE),
    SOFT(Material.COAL_ORE, "Soft Band", 1, ChatColor.RED),
    MEDIUM(Material.IRON_ORE, "Medium Band", 2, ChatColor.YELLOW),
    HARD(Material.GOLD_ORE, "Hard Band", 3, ChatColor.WHITE),
    INTER(Material.EMERALD_ORE, "Intermediate Band", 4, ChatColor.GREEN),
    WET(Material.DIAMOND_ORE, "Wet Band", 5, ChatColor.DARK_BLUE),
    BRIDGESTONE(Material.REDSTONE_ORE, "Bridgestone Tyre", 6, ChatColor.DARK_GRAY);


    @Getter
    private final Material material;
    @Getter
    final String name;
    @Getter
    final int tyreID;
    @Getter
    final ChatColor color;

    @Setter @Getter
    private TyreConfigData data;

    Tyres(Material material, String name, int tyreID, ChatColor color) {
        this.material = material;
        this.name = name;
        this.tyreID= tyreID;
        this.color = color;
    }


    public static Tyres getTyreFromID(int tyreID) {
        for (Tyres value : Tyres.values()) {
            if(value.getTyreID() == tyreID) {
                return value;
            }
        }
        return null;
    }
}
