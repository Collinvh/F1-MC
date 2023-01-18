package collinvht.projectr.manager;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.*;
import collinvht.projectr.commands.fia.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class CommandManager {

    public static void initializeCommands() {
        registerCommand("race", new RaceCommand());
        registerCommand("slowdown", new BlockSlowDown());
        registerCommand("timetrial", new TimetrialCommand());
        registerCommand("raceteam", new TeamCommand());
        registerCommand("tuktuk", new TukTukCommand());

        /*
        FIA Commands
         */
        registerCommand("announcement", new Announcement());
        registerCommand("dsq", new DSQCommand());
        registerCommand("penalty", new PenaltyCommand());
        registerCommand("warning", new WarningCommand());
        registerCommand("flag", new FlagCommand());
    }

    private static void registerCommand(String command, CommandExecutor executor) {
        registerCommand(command, executor, null);
    }

    private static void registerCommand(String stringCommand, CommandExecutor executor, TabCompleter completer) {
        PluginCommand command = ProjectR.getInstance().getCommand(stringCommand);
        if(command != null) {
            command.setExecutor(executor);
            if (completer != null) command.setTabCompleter(completer);
        }
    }
}
