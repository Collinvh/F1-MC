package collinvht.f1mc.module.buildingtools.commands;

import collinvht.f1mc.module.buildingtools.commands.command.CustomReplace;
import collinvht.f1mc.module.buildingtools.commands.command.CustomReplacenear;
import collinvht.f1mc.module.buildingtools.commands.command.CustomSet;
import collinvht.f1mc.module.buildingtools.commands.command.CustomUndo;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class BTCommands extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("creplace", new CustomReplace(), new CustomReplace());
        registerCommand("cset", new CustomSet(), new CustomSet());
        registerCommand("cundo", new CustomUndo());
    }
}
