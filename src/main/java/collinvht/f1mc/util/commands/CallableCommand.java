package collinvht.f1mc.util.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CallableCommand<V> {

    V call(CommandSender sender, Command command, String label, String[] args) throws Exception;
}
