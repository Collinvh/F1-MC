package collinvht.f1mc.util.modules;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public abstract class CommandModuleBase extends ModuleBase {
    public void registerCommand(String name, CommandUtil commandUtil) {
        this.registerCommand(name, commandUtil, null);
    }
    public void registerCommand(String name, CommandUtil commandUtil, TabCompleter completer) {
        F1MC instance = F1MC.getInstance();
        PluginCommand command = instance.getCommand(name);
        if(command != null) {
            command.setExecutor(commandUtil);
            if(completer != null) {
                command.setTabCompleter(completer);
            }
        }
    }
}
