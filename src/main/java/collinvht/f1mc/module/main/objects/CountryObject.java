package collinvht.f1mc.module.main.objects;

import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.util.Utils;
import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.NametagAPI;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.implementation.head.Head;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class CountryObject {
    private final String countryName;
    private final String countryShort;
    private final String countryImg;
    private final int headID;
    private ItemStack stack;
    @Setter
    private ArrayList<UUID> players = new ArrayList<>();

    public CountryObject(String countryName, String countryShort, String countryImg, int id) {
        this.countryName = countryName;
        this.countryShort = countryShort;
        this.countryImg = countryImg;
        String name = ChatColor.RESET + "" + ChatColor.GRAY + countryName.substring(0, 1).toUpperCase() + countryName.substring(1).replace("_", " ");
        if(id == 0) {
            List<Head> heads = HeadAPI.getHeadsByName(countryName.replace("_", " "));
            if (!heads.isEmpty()) {
                for (Head head : heads) {
                    if(head.getTags().contains("Flags")) {
                        this.headID = head.getId();
                        this.stack = Utils.createSkull(headID, name);
                        return;
                    }
                }
            }
            this.headID = 223;
        } else this.headID = id;
    }

    public void addPlayer(Player player) {
        CountryManager.getPlayerPerCountry().put(player.getUniqueId(), this);
        players.add(player.getUniqueId());
        updateTag(player);
    }

    public ItemStack getStack() {
        if(stack != null) {
            if(stack.getItemMeta() != null) {
                if(stack.getType() != Material.AIR) {
                    return stack;
                }
            }
        }
        String name = ChatColor.RESET + "" + ChatColor.GRAY + countryName.substring(0, 1).toUpperCase() + countryName.substring(1).replace("_", " ");
        this.stack = Utils.createSkull(headID, name);
        return stack;
    }

    public void updateTag(Player player) {
        NametagEdit.getApi().updatePlayerPrefix(player.getName(), countryImg + " ");
    }
}
