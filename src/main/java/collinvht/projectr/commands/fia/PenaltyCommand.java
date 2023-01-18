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

public class PenaltyCommand extends CommandUtil {

    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        /*
        This is to penalize the player.
         */
        addPart("%", 2, "/penalty [player_name] [reason]", ((sender, command, label, args) -> {
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

                if(PenaltyManager.addPenalty(player.getUniqueId(), builder.toString())) {
                    player.sendMessage(prefix + "You had an penalty applied to you, reason: \n" + builder);
                }
                return "Penalty Added.";
            } else {
                return "Their is currently no ongoing race.";
            }
        }), Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
    }
}
