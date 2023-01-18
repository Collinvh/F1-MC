package collinvht.projectr.commands.fia;

import collinvht.projectr.listener.MTListener;
import collinvht.projectr.util.enums.Permissions;
import collinvht.projectr.util.objects.commands.CommandUtil;
import collinvht.projectr.util.objects.race.RaceDriver;
import collinvht.projectr.util.objects.race.RaceListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Announcement extends CommandUtil {

    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        /*
        This is to send an announcement to the current server.
         */
        addPart("%", 1, "/announcement [text]", ((sender, command, label, args) -> {
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i<args.length; i++) {
                builder.append(args[i]).append(" ");
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(prefix + builder);
            }
            return "Announcement sent";
        }), Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
    }
}
