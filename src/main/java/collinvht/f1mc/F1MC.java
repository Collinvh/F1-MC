package collinvht.f1mc;

import collinvht.f1mc.module.ModuleManager;
import collinvht.f1mc.util.Utils;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;

public final class F1MC extends JavaPlugin {
    @Getter
    private static F1MC instance;
    @Getter
    private static final Timer F1Timer = new Timer("F1MC_GENERAL_TIMER");
    @Override
    public void onEnable() {
        instance = this;
        Utils.setupConfig(this);
        ModuleManager.loadModules();
    }
    @Override
    public void onDisable() {
        ModuleManager.saveModules();
        F1Timer.cancel();
    }
}
