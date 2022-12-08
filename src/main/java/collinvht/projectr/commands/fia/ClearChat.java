package collinvht.projectr.commands.fia;

import collinvht.projectr.commands.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ClearChat implements CommandUtil {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("projectr.fia")) {

            for(int i = 0; i<200; i++) {
                Bukkit.broadcastMessage("");
            }
            CommandUtil.sendMessageToServer(ChatColor.RED +" Chat is gecleared.");
            sender.sendMessage(serverPrefix + "Chat gecleared");
        } else {
            sender.sendMessage(serverPrefix + "Geen permissie.");
        }
        return true;
    }
}
