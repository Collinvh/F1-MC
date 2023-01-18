package collinvht.projectr.commands;

import collinvht.projectr.manager.race.TeamManager;
import collinvht.projectr.util.enums.Permissions;
import collinvht.projectr.util.objects.commands.CommandUtil;
import collinvht.projectr.util.objects.vehicle.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static collinvht.projectr.manager.race.TeamManager.*;
/*
Command /raceteam
 */
public class TeamCommand extends CommandUtil {
    @Override
    public void initializeCommand(@NotNull CommandSender commandSender) {
        boolean hasFIAPerms = Permissions.FIA_ADMIN.hasPermission(commandSender) || Permissions.FIA_TEAM.hasPermission(commandSender);

        /*
        This creates a team, and it's needed roles for LuckPerms
         */
        addPart("create", 1, "/raceteam create [team_name]", (sender, command, label, args) -> createNewTeam(args[1].toLowerCase()), Permissions.FIA_TEAM, Permissions.FIA_ADMIN);
         /*
        This sets a value of the team,
        /raceteam set [team_name] color [color]
        /raceteam set [team_name] owner [ownerName]
        /raceteam set [team_name] prefix [prefix]
        /raceteam set [team_name] name [prefix]
         */
        addPart("set", 3, "/raceteam set [team_name] [type] [input]", (sender, command, label, args) -> setTeam(args[1].toLowerCase(), args[2].toLowerCase(), args[3]), Permissions.FIA_TEAM, Permissions.FIA_ADMIN);
         /*
        This adds a member of the team with the given parameters
         */
        addPart("add", 2, "/raceteam add [team_name] [player_name]", (sender, command, label, args) -> {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                return "Player doesn't exist";
            }
            return addMember(args[1].toLowerCase(), player.getUniqueId());
        }, Permissions.FIA_TEAM, Permissions.FIA_ADMIN);

        /*
        This removes a member of the team with the given parameters
         */
        addPart("remove", 2, "/raceteam remove [team_name] [player_name]", (sender, command, label, args) -> {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                return "Player doesn't exist";
            }
            return removeMember(args[1].toLowerCase(), player.getUniqueId());
        }, Permissions.FIA_TEAM, Permissions.FIA_ADMIN);

        /*
        This deletes the team with the given parameters
         */
        addPart("delete", 1, "/raceteam delete [team_name]", (sender, command, label, args) -> deleteTeam(args[1].toLowerCase()), Permissions.FIA_TEAM, Permissions.FIA_ADMIN);
        /*
        This lists every team currently loaded
         */
        addPart("list", 0, "/raceteam list", (sender, command, label, args) -> TeamManager.listTeams(), Permissions.FIA_TEAM, Permissions.FIA_ADMIN);

        /*
        FIA can't accept kick or leave any team.
         */
        if(!hasFIAPerms) {
            /*
            Send a request to a team
             */
            addPart("request", 1, "/raceteam request [team_name]", (sender, command, label, args) -> request(args[1].toLowerCase(), ((Player) sender).getUniqueId()));
            /*
            This is for owners only,
            they can accept a member who has requested to join
             */
            addPart("accept", 1, "/raceteam accept [player_name]", (sender, command, label, args) -> {
                if(sender instanceof Player) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        return "Player doesn't not exist";
                    }
                    return accept(((Player) sender).getUniqueId(), player.getUniqueId());
                } else {
                    return "This is only possible as a player";
                }
            });
            /*
            This is for owners only,
            they can kick any members they desire.
             */
            addPart("kick", 1, "/raceteam kick [player_name]", (sender, command, label, args) -> {
                if (sender instanceof Player) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        return "Player doesn't exist";
                    }
                    return kickPlayer(((Player) sender).getUniqueId(), player.getUniqueId());
                } else {
                    return "This is only possible as a player";
                }
            });
            /*
            This is for members only,
            this is to leave your current team.
            Owners can't leave their team.
             */
            addPart("leave", 0, "/raceteam leave", (sender, command, label, args) -> {
                if(sender instanceof Player) {
                    return leave(((Player) sender).getUniqueId());
                } else {
                    return "This is only possible as a player";
                }
            });
        }
    }
}
