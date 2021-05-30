package collinvht.zenticracing.commands.fia;

import collinvht.zenticracing.commands.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ClearChat implements CommandUtil {
    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > ";
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("zentic.fia")) {

            for(int i = 0; i<200; i++) {
                Bukkit.broadcastMessage("");
            }
            sendMessageToServer(ChatColor.RED +" Chat is gecleared.");
            sender.sendMessage(zentic + "Chat gecleared");
        } else {
            sender.sendMessage(zentic + "Geen permissie.");
        }
        return true;
    }
}
