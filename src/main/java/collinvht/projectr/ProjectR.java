package collinvht.projectr;

import collinvht.projectr.listener.MTListener;
import collinvht.projectr.manager.CommandManager;
import collinvht.projectr.manager.RacingManager;
import collinvht.projectr.util.WorldEditUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProjectR extends JavaPlugin {
    @Getter
    private static ProjectR instance;

    @Getter
    private static final String pluginPrefix = "" + ChatColor.YELLOW + ChatColor.BOLD + "R > " + ChatColor.RESET;

    @Override
    public void onEnable() {
        instance = this;

        /*
        Initialize Managers
         */
        RacingManager.initialize();
        CommandManager.initializeCommands();

        /*
        Initialize Utils
         */
        WorldEditUtil.initialize();

        /*
        Initialize Listeners
         */
        MTListener.initialize();
    }

    @Override
    public void onDisable() {

        /*
        Disable Managers
         */
        RacingManager.disable();
    }
}
