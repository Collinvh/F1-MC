package collinvht.f1mc.module.buildingtools.command;

import collinvht.f1mc.module.buildingtools.command.commands.BuildingTools;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class BuildingToolsCommands extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("buildingtools", new BuildingTools());
    }
}
