package collinvht.f1mc.module.racing.module.fia.command;

import collinvht.f1mc.module.racing.module.fia.command.commands.*;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class FiaCommands extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("flag", new FlagCommand());
        registerCommand("dsq", new DSQCommand(), new DSQCommand());
        registerCommand("penalty", new PenaltyCommand());
        registerCommand("warning", new WarningCommand());
        registerCommand("announcement", new AnnouncementCommand());
        registerCommand("helpfia", new HelpFIACommand());
    }
}
