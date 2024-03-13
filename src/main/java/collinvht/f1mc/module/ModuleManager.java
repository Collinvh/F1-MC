package collinvht.f1mc.module;

import collinvht.f1mc.module.discord.DiscordModule;
import collinvht.f1mc.module.buildingtools.BuildingToolsModule;
import collinvht.f1mc.module.vehiclesplus.VehiclePlusModule;
import collinvht.f1mc.util.modules.ModuleBase;
import collinvht.f1mc.module.main.MainModule;
import collinvht.f1mc.module.racing.RacingModule;

import java.util.ArrayList;

public class ModuleManager {
    private static final ArrayList<ModuleBase> modules = new ArrayList<>();

    public static void loadModules() {
        modules.add(new MainModule());
        modules.add(new DiscordModule());
        modules.add(new VehiclePlusModule());
        modules.add(new RacingModule());
        modules.add(new BuildingToolsModule());
    }

    public static void saveModules() {
        for (ModuleBase module : modules) {
            module.save();
        }
    }
}
