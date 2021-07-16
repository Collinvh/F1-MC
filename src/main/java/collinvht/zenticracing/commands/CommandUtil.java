package collinvht.zenticracing.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public interface CommandUtil extends CommandExecutor {

    String prefix = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;


    default void sendUsage(CommandSender sender, String... usages) {
        sender.sendMessage(prefix + "Usage is \n" + Arrays.toString(usages));
    }

    static void sendMessageToServer(String mesage) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(mesage);
        }
    }

    static void sendMessageToServerWithPermission(String mesage, String permission) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission(permission)) {
                player.sendMessage(mesage);
            }
        }
    }
}
