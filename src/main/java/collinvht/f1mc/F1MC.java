package collinvht.f1mc;

import collinvht.f1mc.module.ModuleManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class F1MC extends JavaPlugin {
    @Getter
    private static F1MC instance;
    @Override
    public void onEnable() {
        instance = this;
        ModuleManager.loadModules();
    }

    @Override
    public void onDisable() {
        ModuleManager.saveModules();
    }
}
