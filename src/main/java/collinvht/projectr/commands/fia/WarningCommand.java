package collinvht.projectr.commands.fia;

import collinvht.projectr.listener.MTListener;
import collinvht.projectr.manager.PenaltyManager;
import collinvht.projectr.util.enums.Permissions;
import collinvht.projectr.util.objects.commands.CommandUtil;
import collinvht.projectr.util.objects.race.RaceDriver;
import collinvht.projectr.util.objects.race.RaceListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WarningCommand extends CommandUtil {

    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        /*
        This is to warn the player.
         */
        addPart("%", 2, "/warning [player_name] [reason]", ((sender, command, label, args) -> {
            if(RaceListener.getInstance().isListeningToAnRace()) {
                Player player = Bukkit.getPlayer(args[1]);
                if(player == null) return "Player doesn't exist";
                RaceDriver driver = MTListener.getRaceDrivers().get(player.getUniqueId());
                if(driver == null) return "Player hasn't driven yet.";
                StringBuilder builder = new StringBuilder();
                if(args.length > 3) {
                    for(int i = 2; i<args.length; i++) {
                        builder.append(args[i]);
                    }
                }

                if(PenaltyManager.addWarning(player.getUniqueId(), builder.toString())) {
                    player.sendMessage(prefix + "You had an warning applied to you, reason: \n" + builder);
                } else {
                    player.sendMessage(prefix + "You exceeded the maximum warnings, you got a penalty instead reason: \n" + builder);
                }
                return "Warning Added.";
            } else {
                return "Their is currently no ongoing race.";
            }
        }), Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
    }
}
