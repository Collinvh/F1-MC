package collinvht.f1mc.module.racing.module.fia.command.commands;

import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PenaltyCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 2, "/penalty [name] [length] [reason]", (sender, command, label, args) -> {
            String playerStr = args[0];
            Player player = Bukkit.getPlayer(playerStr);
            if(player != null) {
                RaceDriver driver = VPListener.getRACE_DRIVERS().get(player.getUniqueId());
                if(driver != null) {
                    if (player.isOnline()) {
                        int length;
                        try {
                            length = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            return prefix + "Invalid Number";
                        }
                        if (length > 0) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                builder.append(args[i]).append(" ");
                            }
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayer.sendMessage(prefix + player.getName() + " +" + ChatColor.RED + length + "s penalty\nREASON: " + builder);
                            }
                            player.sendTitle(ChatColor.RED + "PENALTY", "You've gotten a penalty.", 2, 15, 2);
                            player.sendMessage(prefix + " You've gotten a " + length + "s penalty!");
                            return prefix + "Penalty applied";
                        } else return prefix + "Number has to be more than 0";
                    } else {
                        return prefix + "That player isn't online";
                    }
                } else return prefix + "That player hasn't driven before";
            } else {
                return prefix + "That player doesn't exist";
            }
        });
    }
}
