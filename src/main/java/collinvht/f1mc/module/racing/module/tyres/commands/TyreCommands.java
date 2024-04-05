package collinvht.f1mc.module.racing.module.tyres.commands;

import collinvht.f1mc.module.racing.module.tyres.commands.command.TyreCommand;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class TyreCommands extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("tyre", new TyreCommand());
    }
}
