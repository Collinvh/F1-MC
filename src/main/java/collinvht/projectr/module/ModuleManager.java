package collinvht.projectr.module;

import collinvht.projectr.module.itemsadder.ItemsAdderModule;
import collinvht.projectr.module.vehiclesplus.VehiclePlusModule;
import collinvht.projectr.util.modules.ModuleBase;
import collinvht.projectr.module.main.MainModule;
import collinvht.projectr.module.racing.RacingModule;

import java.util.ArrayList;

public class ModuleManager {
    private static final ArrayList<ModuleBase> modules = new ArrayList<>();

    public static void loadModules() {
        modules.add(new MainModule());
        modules.add(new VehiclePlusModule());
        modules.add(new RacingModule());
        modules.add(new ItemsAdderModule());
    }

    public static void saveModules() {
        for (ModuleBase module : modules) {
            module.save();
        }
    }
}
