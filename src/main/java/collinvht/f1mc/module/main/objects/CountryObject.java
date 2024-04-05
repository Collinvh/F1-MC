package collinvht.f1mc.module.main.objects;

import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.util.Utils;
import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.NametagAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class CountryObject {
    private String countryName;
    private String countryShort;
    private String countryImg;
    @Setter
    private ArrayList<UUID> players = new ArrayList<>();

    public CountryObject(String countryName, String countryShort, String countryImg) {
        this.countryName = countryName;
        this.countryShort = countryShort;
        this.countryImg = countryImg;
    }

    public void update(String countryName, String countryShort, String countryImg) {
        this.countryName = countryName;
        this.countryShort = countryShort;
        this.countryImg = countryImg;
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                if (player.isOnline()) {
                    updateTag(player);
                }
            }
        }
    }

    public void addPlayer(Player player) {
        CountryManager.getPlayerPerCountry().put(player.getUniqueId(), this);
        players.add(player.getUniqueId());
        updateTag(player);
    }

    public void updateTag(Player player) {
        NametagEdit.getApi().updatePlayerPrefix(player.getName(), countryImg + " ");
    }
}
