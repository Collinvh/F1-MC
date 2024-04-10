package collinvht.f1mc.module.main.command;

import collinvht.f1mc.module.main.command.commands.CountryCommand;
import collinvht.f1mc.module.main.command.commands.DiscordCommand;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class MainCommands extends CommandModuleBase {
    @Override
    public void load() {
        if(Utils.isEnableCountryModule()) {
            registerCommand("country", new CountryCommand());
        }
        registerCommand("discord", new DiscordCommand());
    }
}
