package collinvht.projectr.util.interfaces;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.Callable;

public interface CallableCommand<V> {

    V call(CommandSender sender, Command command, String label, String[] args) throws Exception;
}
