package collinvht.projectr;

import collinvht.projectr.manager.RacingManager;
import collinvht.projectr.old.commands.Commands;
import collinvht.projectr.old.commands.racing.setup.SetupManager;
import collinvht.projectr.old.commands.team.Team;
import collinvht.projectr.old.listener.Listeners;
import collinvht.projectr.old.listener.VPPListener;
import collinvht.projectr.old.util.ConfigUtil;
import collinvht.projectr.old.util.Utils;
import collinvht.projectr.old.util.objs.DiscordUtil;
import collinvht.projectr.old.util.objs.JSONUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProjectR extends JavaPlugin {
    @Getter
    private static ProjectR racing;

    @Getter
    private static final String pluginPrefix = "" + ChatColor.YELLOW + ChatColor.BOLD + "R > " + ChatColor.RESET;

    @Override
    public void onEnable() {
        racing = this;

        /*
        Initialize Managers
         */
        RacingManager.initialize();
    }

    @Override
    public void onDisable() {

        /*
        Disable Managers
         */
        RacingManager.disable();
    }
}
