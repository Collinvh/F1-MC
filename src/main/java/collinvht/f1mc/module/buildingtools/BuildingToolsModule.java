package collinvht.f1mc.module.buildingtools;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.buildingtools.listener.BuildingToolsListeners;
import collinvht.f1mc.module.buildingtools.commands.BTCommands;
import collinvht.f1mc.util.modules.ModuleBase;

public class BuildingToolsModule extends ModuleBase {
    @Override
    public void load() {
        F1MC.getLog().info("[F1MC] [BT] Enabling BuildingTools Module");
        attachModule(new BuildingToolsListeners());
        attachModule(new BTCommands());
        F1MC.getLog().info("[F1MC] [BT] Enabled BuildingTools Module");
    }
}
