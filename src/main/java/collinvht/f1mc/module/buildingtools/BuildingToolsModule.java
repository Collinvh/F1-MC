package collinvht.f1mc.module.buildingtools;

import collinvht.f1mc.module.buildingtools.listener.BuildingToolsListeners;
import collinvht.f1mc.util.modules.ModuleBase;
import org.bukkit.Bukkit;

public class BuildingToolsModule extends ModuleBase {
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [BT] Enabling BuildingTools Module");
        attachModule(new BuildingToolsListeners());
        Bukkit.getLogger().info("[F1MC] [BT] Enabled BuildingTools Module");
    }
}
