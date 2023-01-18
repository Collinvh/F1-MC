package collinvht.projectr.commands.fia;

import collinvht.projectr.listener.MTListener;
import collinvht.projectr.util.enums.Permissions;
import collinvht.projectr.util.objects.commands.CommandUtil;
import collinvht.projectr.util.objects.race.RaceDriver;
import collinvht.projectr.util.objects.race.RaceListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DSQCommand extends CommandUtil {

    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        /*
        This is to DSQ the player from the current race.
         */
        addPart("%", 0, "/dsq [player_name]", ((sender, command, label, args) -> {
            if(RaceListener.getInstance().isListeningToAnRace()) {
                Player player = Bukkit.getPlayer(args[0]);
                if(player == null) return "Player doesn't exist";
                RaceDriver driver = MTListener.getRaceDrivers().get(player.getUniqueId());
                if(driver == null) return "Player hasn't driven yet.";
                if(driver.isDisqualified()) return "Driver is already disqualified.";
                driver.setDisqualified(true);
                player.sendMessage(prefix + ChatColor.DARK_RED + "You've been disqualified, please return to the pit lane.");
                return "Player has been disqualified.";
            } else {
                return "Their is currently no ongoing race.";
            }
        }), Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
    }
}
