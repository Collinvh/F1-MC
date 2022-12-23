package collinvht.projectr.commands;

import collinvht.projectr.ProjectR;
import org.bukkit.command.CommandExecutor;

public interface CommandUtil extends CommandExecutor {
    String prefix = ProjectR.getPluginPrefix();
}
