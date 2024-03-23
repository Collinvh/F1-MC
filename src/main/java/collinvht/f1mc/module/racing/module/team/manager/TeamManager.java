package collinvht.f1mc.module.racing.module.team.manager;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.team.object.TeamObject;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.track.Track;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class TeamManager extends ModuleBase {
    @Getter
    private static TeamManager instance;

    private static final HashMap<String, TeamObject> TEAMS = new HashMap<>();

    private static LuckPerms luckPerms;
    private static final HashMap<TeamObject, Group> GROUPS = new HashMap<>();

    public void load() {
        luckPerms = Utils.getLuckperms();
        if(luckPerms == null)  {
            setInitialized(false);
            Bukkit.getPluginManager().disablePlugin(F1MC.getInstance());
            return;
        }
        instance = this;

        loadTeams();
    }

    public void saveModule() {
        instance.saveTeams();
    }

    public static String createNewTeam(String name) {
        TeamObject team = new TeamObject(name);
        String str = createTeam(team);
        TEAMS.put(name, team);
        return str;
    }

    public static String createTeam(TeamObject team) {
        if(TEAMS.containsKey(team.getTeamName())) return "A team with that name already exists.";
        Group group = luckPerms.getGroupManager().getGroup(team.getTeamName().toLowerCase());
        if(group == null) {
            try {
                group = luckPerms.getGroupManager().createAndLoadGroup(team.getTeamName()).get();
                group.getData(DataType.NORMAL).add(PrefixNode.builder().prefix( ChatColor.DARK_GRAY + "|" + team.getTeamColor() + team.getTeamPrefix() + ChatColor.DARK_GRAY +"| " + ChatColor.RESET).withContext("server", "racing").priority(10).build());
                group.getData(DataType.NORMAL).add(PermissionNode.builder("blocklocker." + team.getTeamName()).permission("team." + team.getTeamName()).withContext("server", "racing").build());
                luckPerms.getGroupManager().saveGroup(group);

                Track track = luckPerms.getTrackManager().getTrack("team");
                if(track == null) {
                    track = luckPerms.getTrackManager().createAndLoadTrack("team").get();
                }
                track.appendGroup(group);
                luckPerms.getTrackManager().saveTrack(track);

                if(!team.getMembers().isEmpty()) {
                    for (UUID member : team.getMembers()) {
                        team.addMember(member);
                    }
                }

                if(team.getOwner() != null) {
                    team.setOwner(team.getOwner());
                }

                return "Team created.";


            } catch (ExecutionException | InterruptedException e ) {
                Bukkit.getLogger().severe(team.getTeamName() + " their group couldn't be created");
                return "Something went wrong.";
            }
        }
        return "Team created.";
    }

    public static String setTeam(String team, String type, String input) {
        TeamObject teamObj = TEAMS.get(team);
        if(teamObj != null) {
            switch (type) {
                case "prefix":
                    teamObj.setTeamPrefix(input);
                    return "Prefix changed.";
                case "name":
                    teamObj.setTeamName(input.toLowerCase());
                    return "Name changed.";
                case "owner":
                    Player player = Bukkit.getPlayer(input);
                    if(player == null) return "Player doesn't exist.";
                    TeamObject team1 = getTeamFromUUID(player.getUniqueId());
                    if(team1 == null) teamObj.addMember(player.getUniqueId());
                    else if(!team1.getTeamName().equalsIgnoreCase(teamObj.getTeamName())) {
                        team1.removeMember(player.getUniqueId());
                        teamObj.removeMember(player.getUniqueId());
                    } else if(team1.getOwner() != null && teamObj.getOwner() != null) if(team1.getOwner().equals(teamObj.getOwner())) return "Die speler is de owner al.";

                    teamObj.setOwner(player.getUniqueId());
                    return "Owner changed";
                case "color":
                    ChatColor color = ChatColor.getByChar(input.toLowerCase().charAt(0));
                    if(color == null) return "Color code is incorrect.";
                    teamObj.setTeamColor(color);
                    return "Color changed.";
                default:
                    return "Invalid type";
            }
        }
        return "Team doesn't exist.";
    }

    public static String addMember(String team, UUID uuid) {
        TeamObject teamObj = TEAMS.get(team);
        if(teamObj != null) {
            TeamObject currentTeam = getTeamFromUUID(uuid);
            if(currentTeam != null) {
                if(currentTeam.getTeamName().equalsIgnoreCase(teamObj.getTeamName())) return "That player is already in a team.";
                currentTeam.removeMember(uuid);
            }
            teamObj.addMember(uuid);
            return "Member added.";
        }
        return "Team doesn't exist.";
    }


    public static String removeMember(String team, UUID uuid) {
        TeamObject teamObj = TEAMS.get(team);
        if(teamObj != null) {
            TeamObject currentTeam = getTeamFromUUID(uuid);
            if(currentTeam != null) currentTeam.removeMember(uuid);
            teamObj.removeMember(uuid);
            if(teamObj.getOwner() == uuid) teamObj.setOwner(null, true);
            return "Member removed.";
        }
        return "Team doesn't exist.";
    }

    public static String deleteTeam(String team) {
        TeamObject teamObj = TEAMS.get(team);
        if(teamObj != null) {
            teamObj.delete();
            TEAMS.remove(team);
            return "Team deleted.";
        }
        return "Team doesn't exist.";
    }

    public static String leave(UUID uuid) {
        TeamObject team = getTeamFromUUID(uuid);
        if(team != null) {
            if(team.getOwner() == uuid) {
                return "You are the owner of this team.";
            }
            team.removeMember(uuid);
            return "You left your team.";
        }
        return "You aren't in a team.";
    }

    public static String accept(UUID owner, UUID uuid) {
        TeamObject team = getTeamFromUUID(owner);
        if(team != null) {
            if(team.getOwner() == owner) {
                if(team.getMembers().contains(uuid)) return "Player is already in your team.";
                TeamObject uuidteam = getTeamFromUUID(uuid);
                if(uuidteam != null) {
                    return "Player is already in a team.";
                }
                if(team.getRequests().contains(uuid)) {
                    team.addMember(uuid);
                    team.getRequests().remove(uuid);
                    return "Player accepted.";
                } else {
                    return "This player did not request to join this team.";
                }
            } else {
                return "Only the owner can do this.";
            }
        }
        return "You aren't in a team.";
    }

    public static String request(String team, UUID uuid) {
        TeamObject teamCurrent = getTeamFromUUID(uuid);
        TeamObject teamNew = TEAMS.get(team);
        if(teamCurrent == null) {
            if(teamNew != null) {
                if(teamNew.getRequests().contains(uuid)) return "You already requested to join this team.";
                teamNew.getRequests().add(uuid);
            } else {
                return "Team doesn't exist.";
            }
        }
        return "You already are in a team, use /team leave to join a new one.";
    }

    public static String kickPlayer(UUID sender, UUID kickedPlayer) {
        TeamObject teamCurrent = getTeamFromUUID(sender);
        if(sender == kickedPlayer) return "You can't kick yourself";
        if(teamCurrent != null) {
            if(teamCurrent.getOwner() != null) {
                if(teamCurrent.getOwner() == sender) {
                    teamCurrent.removeMember(kickedPlayer);
                    return "Player kicked.";
                } else {
                    return "Only the owner can do this.";
                }
            } else {
                return "Only the owner can do this.";
            }
        } else {
            return "You aren't in a team.";
        }
    }

    public static String listTeams() {
        if(TEAMS.size() == 0) return "No teams are created yet.";
        StringBuilder builder = new StringBuilder();
        builder.append("Team list\n");
        TEAMS.forEach((s, team) -> builder.append(s).append("\n"));
        return builder.toString();
    }

    private static TeamObject getTeamFromUUID(UUID uuid) {
        AtomicReference<TeamObject> team = new AtomicReference<>();
        TEAMS.forEach((s, team1) -> {
            if(team1.getMembers().contains(uuid)) {
                team.set(team1);
            }
        });
        return team.get();
    }

    private void loadTeams() {
        File raceFiles = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/teams/").toFile();
        if(raceFiles.exists()) {
            File[] races = raceFiles.listFiles();
            if(races != null) {
                for (File teamFile : races) {
                    try {
                        TeamObject team = TeamObject.fromJson(Utils.readJson(teamFile.getAbsolutePath()).getAsJsonObject());
                        if(team != null) {
                            createTeam(team);
                            TEAMS.put(team.getTeamName().toLowerCase(), team);
                        }
                    } catch (Exception e) {
                        Bukkit.getLogger().warning(teamFile.getAbsolutePath() + " failed to load.");
                    }
                }
            }
        }
    }

    private void saveTeams() {
        if(!TEAMS.isEmpty()) {
            TEAMS.forEach((s, team) ->  {
                team.saveJson();
            });
        }
    }
}
