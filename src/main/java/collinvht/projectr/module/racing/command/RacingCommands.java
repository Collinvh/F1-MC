package collinvht.projectr.module.racing.command;

import collinvht.projectr.module.racing.command.commands.RaceCommand;
import collinvht.projectr.util.modules.CommandModuleBase;

public class RacingCommands extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("race", new RaceCommand());
    }
}
