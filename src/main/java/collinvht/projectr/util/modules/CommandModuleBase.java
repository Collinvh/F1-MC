package collinvht.projectr.util.modules;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.commands.CommandUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public abstract class CommandModuleBase extends ModuleBase {
    public void registerCommand(String name, CommandUtil commandUtil) {
        this.registerCommand(name, commandUtil, null);
    }
    public void registerCommand(String name, CommandUtil commandUtil, TabCompleter completer) {
        ProjectR instance = ProjectR.getInstance();
        PluginCommand command = instance.getCommand(name);
        if(command != null) {
            command.setExecutor(commandUtil);
            if(completer != null) {
                command.setTabCompleter(completer);
            }
        }
    }

    @Override
    public final void saveModule() {}
}
