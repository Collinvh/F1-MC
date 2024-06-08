package collinvht.f1mc;

import collinvht.f1mc.module.ModuleManager;
import collinvht.f1mc.util.Utils;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import java.util.logging.Logger;

public final class F1MC extends JavaPlugin {
    @Getter
    private static F1MC instance;
    @Getter
    private static AsyncScheduler asyncScheduler;
    @Getter
    private static BukkitScheduler scheduler;
    @Getter
    private static Logger log;
    @Override
    public void onEnable() {
        instance = this;
        asyncScheduler = getServer().getAsyncScheduler();
        scheduler = getServer().getScheduler();
        log = getLogger();
        Utils.setupConfig(this);
        ModuleManager.loadModules();
    }
    @Override
    public void onDisable() {
        ModuleManager.saveModules();
    }
}
