package collinvht.projectr;

import collinvht.projectr.module.ModuleManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProjectR extends JavaPlugin {
    @Getter
    private static ProjectR instance;
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
