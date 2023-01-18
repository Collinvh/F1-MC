package collinvht.projectr;

import collinvht.projectr.listener.InventoryListener;
import collinvht.projectr.listener.ItemsAdderListener;
import collinvht.projectr.listener.MTListener;
import collinvht.projectr.manager.*;
import collinvht.projectr.manager.race.RacingManager;
import collinvht.projectr.manager.vehicle.SetupManager;
import collinvht.projectr.manager.race.TeamManager;
import collinvht.projectr.manager.vehicle.SlowDownManager;
import collinvht.projectr.manager.TimeTrialHandler;
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
        TeamManager.initialize();
        SetupManager.initialize();
        CommandManager.initializeCommands();
        SlowDownManager.initialize();
        TimeTrialHandler.initialize();

        /*
        Initialize Listeners
         */
        MTListener.initialize();
        InventoryListener.initialize();
        ItemsAdderListener.initialize();
    }

    @Override
    public void onDisable() {

        /*
        Disable Managers
         */
        RacingManager.disable();
        SetupManager.disable();
        TeamManager.disable();
        SlowDownManager.disable();
    }
}
