package collinvht.projectr.util.modules;

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
            attachedModule.save();
        }
        saveModule();
    }

    public abstract void load();
    public abstract void saveModule();
}
