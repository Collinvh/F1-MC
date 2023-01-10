package collinvht.projectr.manager;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.BlockSlowDown;
import collinvht.projectr.commands.RaceCommand;
import collinvht.projectr.commands.Timetrial;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class CommandManager {

    public static void initializeCommands() {
        registerCommand("race", new RaceCommand());
        registerCommand("blockslowdown", new BlockSlowDown());
        registerCommand("timetrial", new Timetrial());
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
