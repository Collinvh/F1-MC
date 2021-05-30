package collinvht.zenticracing.commands;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.fia.*;
import collinvht.zenticracing.commands.racing.PersoonlijkRecord;
import collinvht.zenticracing.commands.racing.RaceManager;
import collinvht.zenticracing.commands.racing.SnelsteCommand;
import collinvht.zenticracing.commands.racing.StandCommand;
import collinvht.zenticracing.commands.team.Golfkar;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.TeamBaan;
import collinvht.zenticracing.commands.util.MuteUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class Commands {
    private static final ZenticRacing plugin = ZenticRacing.getRacing();


    public static void initializeCommands() {
        addCommand("race", new RaceManager());
        addCommand("persoonlijkrecord", new PersoonlijkRecord());
        addCommand("raceteam", new Team());
        addCommand("snelste", new SnelsteCommand());
        addCommand("stand", new StandCommand());
        addCommand("teambaan", new TeamBaan());
        addCommand("dsq", new DSQ());
        addCommand("flag", new Flag());
        addCommand("lights", new Lights());
        addCommand("garage", new Garage());
        addCommand("clearchat", new ClearChat());
        addCommand("muteutil", new MuteUtil());
        addCommand("penalty", new Penalty());
        addCommand("warning", new Warning());
        addCommand("golfkar", new Golfkar());
    }


    private static void addCommand(String command, CommandExecutor executor) {
        addCommand(command, executor, null);
    }

    private static void addCommand(String command, CommandExecutor executor, TabCompleter completer) {
        PluginCommand cmd = plugin.getCommand(command);
        if(cmd != null) {
            cmd.setExecutor(executor);
            if(completer != null) {
                cmd.setTabCompleter(completer);
            }
        }
    }
}
