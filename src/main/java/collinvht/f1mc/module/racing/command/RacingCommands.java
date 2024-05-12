package collinvht.f1mc.module.racing.command;

import collinvht.f1mc.module.racing.command.commands.RaceCommand;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class RacingCommands extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("race", new RaceCommand(), new RaceCommand());
    }
}
