package collinvht.projectr;

import collinvht.projectr.listener.InventoryListener;
import collinvht.projectr.listener.MTListener;
import collinvht.projectr.manager.*;
import collinvht.projectr.manager.race.RacingManager;
import collinvht.projectr.manager.race.SetupManager;
import collinvht.projectr.manager.vehicle.SlowDownManager;
import collinvht.projectr.manager.race.TimeTrialHandler;
import collinvht.projectr.util.WorldEditUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProjectR extends JavaPlugin {
    @Getter
    private static ProjectR instance;

    @Getter
    private static final String pluginPrefix = "" + ChatColor.YELLOW + ChatColor.BOLD + ":projectr: > " + ChatColor.RESET;

    @Override
    public void onEnable() {
        instance = this;

        /*
        Initialize Managers
         */
        RacingManager.initialize();
        SetupManager.initialize();
        CommandManager.initializeCommands();
        SlowDownManager.initialize();
        TimeTrialHandler.initialize();

        /*
        Initialize Utils
         */
        WorldEditUtil.initialize();

        /*
        Initialize Listeners
         */
        MTListener.initialize();
        InventoryListener.initialize();
    }

    @Override
    public void onDisable() {

        /*
        Disable Managers
         */
        RacingManager.disable();
        SetupManager.disable();
        SlowDownManager.disable();
    }
}
