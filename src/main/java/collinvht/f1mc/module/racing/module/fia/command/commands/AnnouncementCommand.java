package collinvht.f1mc.module.racing.module.fia.command.commands;

import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AnnouncementCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        if(Permissions.FIA_COMMON.hasPermission(commandSender)) {
            addPart("%", 0, "/announcement [text]", ((sender, command, label, args) -> {
                StringBuilder builder = new StringBuilder();
                for (String arg : args) {
                    builder.append(arg).append(" ");
                }
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendMessage(prefix + builder);
                }
                return prefix + "Message sent";
            }));
        }
    }
}
