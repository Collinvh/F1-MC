package collinvht.f1mc.module.buildingtools;

import collinvht.f1mc.module.buildingtools.command.BuildingToolsCommands;
import collinvht.f1mc.module.buildingtools.listener.BuildingToolsListeners;
import collinvht.f1mc.util.modules.ModuleBase;

public class BuildingToolsModule extends ModuleBase {
    @Override
    public void load() {
        attachModule(new BuildingToolsCommands());
        attachModule(new BuildingToolsListeners());
    }
}
