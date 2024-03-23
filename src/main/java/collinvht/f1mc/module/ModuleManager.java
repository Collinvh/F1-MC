package collinvht.f1mc.module;

import collinvht.f1mc.module.discord.DiscordModule;
import collinvht.f1mc.module.buildingtools.BuildingToolsModule;
import collinvht.f1mc.module.racing.util.RacingMessages;
import collinvht.f1mc.module.vehiclesplus.VehiclePlusModule;
import collinvht.f1mc.util.modules.ModuleBase;
import collinvht.f1mc.module.main.MainModule;
import collinvht.f1mc.module.racing.RacingModule;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class ModuleManager {
    private static final ArrayList<ModuleBase> modules = new ArrayList<>();

    public static void loadModules() {
        Bukkit.getLogger().info("[F1MC] Enabling All Modules");
        modules.add(new MainModule());
        modules.add(new DiscordModule());
        modules.add(new VehiclePlusModule());
        modules.add(new RacingModule());
        modules.add(new BuildingToolsModule());
        Bukkit.getLogger().info("[F1MC] Enabled All Modules");
        Bukkit.getLogger().info("[F1MC] F1MC Initialized");
    }

    public static void saveModules() {
        for (ModuleBase module : modules) {
            module.save();
        }
    }
}
