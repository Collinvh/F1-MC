package collinvht.f1mc.module.racing.module.team.command;

import collinvht.f1mc.module.racing.module.team.manager.TeamManager;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static collinvht.f1mc.module.racing.module.team.manager.TeamManager.*;

public class RaceTeamCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        Permissions.Permission invertedFIA = Permissions.FIA_TEAM.invertPerms();
        Permissions.Permission invertedAdmin = Permissions.FIA_ADMIN.invertPerms();
        /*
        This creates a team, and it's needed roles for LuckPerms
         */
        addPart("create", 1, "/raceteam create [team_name]", (sender, command, label, args) -> prefix + createNewTeam(args[1].toLowerCase()), Permissions.FIA_TEAM, Permissions.FIA_ADMIN);
        /*
        This spawns a car for the team and links it to the team
         */
        addPart("spawn", 2, "/raceteam spawn [team_name] [carmodel]", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                return prefix + spawnCar(args[1].toLowerCase(), (Player) sender, args[2]);
            } else {
                return prefix + "Only a player can do that";
            }
        }, Permissions.FIA_ADMIN, Permissions.FIA_ADMIN);
         /*
        This sets a value of the team,
        /raceteam set [team_name] color [color]
        /raceteam set [team_name] owner [ownerName]
        /raceteam set [team_name] prefix [prefix]
        /raceteam set [team_name] name [prefix]
         */
        addPart("set", 3, "/raceteam set [team_name] [type] [input]", (sender, command, label, args) -> prefix + setTeam(args[1].toLowerCase(), args[2].toLowerCase(), args[3]), Permissions.FIA_TEAM, Permissions.FIA_ADMIN);
         /*
        This adds a member of the team with the given parameters
         */
        addPart("add", 2, "/raceteam add [team_name] [player_name]", (sender, command, label, args) -> {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                return prefix + "Player doesn't exist";
            }
            return prefix + addMember(args[1].toLowerCase(), player.getUniqueId());
        }, Permissions.FIA_TEAM, Permissions.FIA_ADMIN);

        /*
        This removes a member of the team with the given parameters
         */
        addPart("remove", 2, "/raceteam remove [team_name] [player_name]", (sender, command, label, args) -> {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                return prefix + "Player doesn't exist";
            }
            return prefix + removeMember(args[1].toLowerCase(), player.getUniqueId());
        }, Permissions.FIA_TEAM, Permissions.FIA_ADMIN);

        /*
        This deletes the team with the given parameters
         */
        addPart("delete", 1, "/raceteam delete [team_name]", (sender, command, label, args) -> prefix + deleteTeam(args[1].toLowerCase()), Permissions.FIA_TEAM, Permissions.FIA_ADMIN);
        /*
        This lists every team currently loaded
         */
        addPart("list", 0, "/raceteam list", (sender, command, label, args) -> prefix + TeamManager.listTeams(), Permissions.FIA_TEAM, Permissions.FIA_ADMIN);

        /*
        Send a request to a team
         */
        addPart("request", 1, "/raceteam request [team_name]", (sender, command, label, args) -> prefix + request(args[1].toLowerCase(), ((Player) sender).getUniqueId()), invertedAdmin, invertedFIA);
        /*
        This is for owners only,
        they can accept a member who has requested to join
         */
        addPart("accept", 1, "/raceteam accept [player_name]", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    return prefix + "Player doesn't not exist";
                }
                return prefix + accept(((Player) sender).getUniqueId(), player.getUniqueId());
            } else {
                return prefix + "This is only possible as a player";
            }
        }, invertedAdmin, invertedFIA);
        /*
        This is for owners only,
        they can kick any members they desire.
         */
        addPart("kick", 1, "/raceteam kick [player_name]", (sender, command, label, args) -> {
            if (sender instanceof Player) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    return prefix + "Player doesn't exist";
                }
                return prefix + kickPlayer(((Player) sender).getUniqueId(), player.getUniqueId());
            } else {
                return prefix + "This is only possible as a player";
            }
        }, invertedAdmin, invertedFIA);
        /*
        This is for members only,
        this is to leave your current team.
        Owners can't leave their team.
         */
        addPart("leave", 0, "/raceteam leave", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                return prefix + leave(((Player) sender).getUniqueId());
            } else {
                return "This is only possible as a player";
            }
        }, invertedAdmin, invertedFIA);
    }
}
