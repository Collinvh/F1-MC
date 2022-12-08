package collinvht.projectr.commands;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.fia.*;
import collinvht.projectr.commands.racing.*;
import collinvht.projectr.commands.team.Golfkar;
import collinvht.projectr.commands.team.Team;
import collinvht.projectr.commands.tyre.GetTyre;
import collinvht.projectr.commands.util.MuteUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class Commands {
    private static final ProjectR plugin = ProjectR.getRacing();


    public static void initializeCommands() {
        addCommand("race", new RaceManager());
        addCommand("persoonlijkrecord", new PersoonlijkRecord());
        addCommand("raceteam", new Team());
        addCommand("snelste", new SnelsteCommand());
        addCommand("stand", new StandCommand());
        addCommand("dsq", new DSQ());
//        addCommand("flag", new Flag());
//        addCommand("lights", new Lights());
        addCommand("garage", new Garage());
        addCommand("clearchat", new ClearChat());
        addCommand("muteutil", new MuteUtil());
        addCommand("penalty", new Penalty());
        addCommand("warning", new Warning());
        addCommand("golfkar", new Golfkar());
        addCommand("testpc", new TestPc());
        addCommand("getband", new GetTyre());
        addCommand("fia", new FIA());
        addCommand("weer", new Weer());
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
