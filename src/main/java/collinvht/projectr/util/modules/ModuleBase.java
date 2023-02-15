package collinvht.projectr.util.modules;

import org.bukkit.Bukkit;

import java.util.ArrayList;

public abstract class ModuleBase {
    private final ArrayList<ModuleBase> attachedModules = new ArrayList<>();
    public ModuleBase() {
        load();
    }
    public final void attachModule(ModuleBase module) {
        if(module == this) return;
        attachedModules.add(module);
    }

    public final void save() {
        for (ModuleBase attachedModule : attachedModules) {
            if(attachedModule == this) {
                Bukkit.getLogger().severe("Saving parent module in child, creates infinite loop\nPlease remove this.");
                return;
            }
            attachedModule.save();
        }
        saveModule();
    }

    public abstract void load();
    public abstract void saveModule();
}
