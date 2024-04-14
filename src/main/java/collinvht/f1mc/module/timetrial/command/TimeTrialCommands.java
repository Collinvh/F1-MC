package collinvht.f1mc.module.timetrial.command;

import collinvht.f1mc.module.timetrial.command.commands.TimeTrialCommand;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class TimeTrialCommands extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("timetrial", new TimeTrialCommand());
    }
}
