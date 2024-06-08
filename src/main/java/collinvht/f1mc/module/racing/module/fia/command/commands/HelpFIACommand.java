package collinvht.f1mc.module.racing.module.fia.command.commands;

import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpFIACommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/helpfia [message]", ((sender, command, label, args) -> {
            if(sender instanceof Player player) {
                StringBuilder builder = new StringBuilder();
                for (String arg : args) {
                    builder.append(arg).append(" ");
                }
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(Permissions.FIA_ADMIN.hasPermission(onlinePlayer)) {
                        //Todo: fix deprecated
                        onlinePlayer.sendMessage(ChatColor.RED + "HELPFIA " + ChatColor.GRAY + " | " + player.getName() + " > " + builder);
                    }
                }
                return prefix + "Message sent";
            }
            return "";
        }));
    }
}
