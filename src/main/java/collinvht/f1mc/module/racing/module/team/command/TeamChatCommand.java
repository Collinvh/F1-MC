package collinvht.f1mc.module.racing.module.team.command;

import collinvht.f1mc.module.racing.module.team.manager.TeamManager;
import collinvht.f1mc.module.racing.module.team.object.TeamObj;
import collinvht.f1mc.util.commands.CommandUtil;
import collinvht.f1mc.util.modules.CommandModuleBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class TeamChatCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 1, "/teamchat [message]",((sender, command, label, args) -> {
            if(sender instanceof Player player) {
                TeamObj teamObj = TeamManager.getTeamForPlayer(player);
                if(teamObj == null) return prefix + "You're not in any team";
                StringBuilder builder = new StringBuilder();
                for (String arg : args) {
                    builder.append(arg).append(" ");
                }

                ArrayList<UUID> members = (ArrayList<UUID>) teamObj.getMembers().clone();
                members.add(teamObj.getOwner());
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(members.contains(onlinePlayer.getUniqueId())) {
                        onlinePlayer.sendMessage(ChatColor.GREEN + "TeamChat " + ChatColor.GRAY +  "| " + builder);
                    }
                }
            }
            return "";
        }));
    }
}
