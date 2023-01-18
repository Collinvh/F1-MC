package collinvht.projectr.util.objects.vehicle;

import collinvht.projectr.ProjectR;
import collinvht.projectr.manager.race.TeamManager;
import collinvht.projectr.util.Utils;
import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Team {
    @Getter @Setter
    private String teamName;
    @Getter @Setter
    private String teamPrefix;

    @Getter
    private UUID owner = null;
    @Getter @Setter
    private ChatColor teamColor = ChatColor.WHITE;

    @Getter @Setter
    private ArrayList<UUID> members = new ArrayList<>();
    @Getter @Setter
    private ArrayList<UUID> requests = new ArrayList<>();

    public Team(String name) {
        this.teamName = name;
        this.teamPrefix = name;
    }
    public void delete() {
        LuckPerms luckPerms = Utils.getLuckperms();

        if(owner != null) {
            Group group = luckPerms.getGroupManager().getGroup("teambaas");
            if (group != null) {
                User user = luckPerms.getUserManager().getUser(owner);
                if (user == null) {
                    Bukkit.getLogger().severe("Owner doesn't exist: " + owner);
                } else if (!user.getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build()).contains(group)) {
                    user.data().remove(InheritanceNode.builder(group).build());
                    luckPerms.getUserManager().saveUser(user);
                }
            }
            owner = null;
        }

        for (UUID member : members) {
            removeMember(member);
        }

        Group teamGroup = luckPerms.getGroupManager().getGroup(teamName);
        if(teamGroup != null) luckPerms.getGroupManager().deleteGroup(teamGroup);

        File path = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/teams/" + teamName + ".json").toFile();
        Bukkit.getLogger().warning(String.valueOf(path.delete()));
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
        updateLP();
    }

    public void setTeamPrefix(String teamPrefix) {
        this.teamPrefix = teamPrefix;
        updateLP();
    }

    public void setTeamColor(ChatColor teamColor) {
        this.teamColor = teamColor;
        updateLP();
    }

    private void updateLP() {
        LuckPerms luckPerms = Utils.getLuckperms();
        Group group = luckPerms.getGroupManager().getGroup(getTeamName().toLowerCase());
        if(group != null) {
            group.getData(DataType.NORMAL).clear();
            group.getData(DataType.NORMAL).add(PrefixNode.builder().prefix( ChatColor.DARK_GRAY + "|" + getTeamColor() + getTeamPrefix() + ChatColor.DARK_GRAY +"| " + ChatColor.RESET).withContext("server", "racing").priority(10).build());
            group.getData(DataType.NORMAL).add(PermissionNode.builder("blocklocker.group." + getTeamName()).permission("team." + getTeamName()).withContext("server", "racing").build());
            luckPerms.getGroupManager().saveGroup(group);
        } else {
            TeamManager.createTeam(this);
        }
    }

    public static Team fromJson(JsonObject json) {
        try {
            String name = json.get("Name").getAsString();
            String prefix = json.get("Prefix").getAsString();
            String color = json.get("TeamColor").getAsString();

            JsonArray requestArray = json.getAsJsonArray("requests");
            JsonArray memberArray = json.getAsJsonArray("members");


            Team team = new Team(name);
            team.setTeamColor(ChatColor.getByChar(color));
            if(json.get("Owner") != null) {
                UUID owner = UUID.fromString(json.get("Owner").getAsString());
                team.setOwner(owner, true);
            }
            ArrayList<UUID> requests = new ArrayList<>();
            ArrayList<UUID> members = new ArrayList<>();

            for (JsonElement jsonElement : requestArray) requests.add(UUID.fromString(jsonElement.getAsString()));

            for (JsonElement jsonElement : memberArray) members.add(UUID.fromString(jsonElement.getAsString()));

            team.setRequests(requests);
            team.setMembers(members);
            team.setTeamPrefix(prefix);

            return team;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveJson() {
        File path = Paths.get(ProjectR.getInstance().getDataFolder() + "/storage/teams/").toFile();

        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("Name", teamName);
        mainObject.addProperty("Prefix", teamPrefix);
        mainObject.addProperty("TeamColor", teamColor.getChar());
        if(owner != null) mainObject.addProperty("Owner", owner.toString());
        JsonArray requests = new JsonArray();
        for (UUID request : this.requests) {
            requests.add(request.toString());
        }
        JsonArray members = new JsonArray();
        for (UUID member : this.members) {
            members.add(member.toString());
        }
        mainObject.add("requests", requests);
        mainObject.add("members", members);

        Utils.saveJSON(path, teamName, mainObject);
    }

    public void setOwner(UUID uuid) {
        setOwner(uuid, false);
    }
    public void setOwner(UUID uuid, boolean silent) {
        if(silent) {
            owner = uuid;
            return;
        }

        LuckPerms luckPerms = Utils.getLuckperms();
        if(luckPerms == null) return;
        Group group = luckPerms.getGroupManager().getGroup("teambaas");
        if(group == null) {
            try {
                Bukkit.getLogger().info("Teambaas group doesn't exist creating...");
                group = luckPerms.getGroupManager().createAndLoadGroup("teambaas").get();
                group.getData(DataType.NORMAL).add(PrefixNode.builder().prefix(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "T" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET).withContext("server", "racing").priority(10).build());
            } catch (ExecutionException | InterruptedException e ) {
                Bukkit.getLogger().severe("Teambaas group couldn't be created");
                return;
            }
        }
        if(owner != null) {

            User user = luckPerms.getUserManager().getUser(owner);
            if(user == null) {
                Bukkit.getLogger().severe("Owner doesn't exist: " + owner);
                return;
            }
            if(!user.getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build()).contains(group)) {
                user.data().remove(InheritanceNode.builder(group).build());
                luckPerms.getUserManager().saveUser(user);
            }
        }
        owner = uuid;

        User user = luckPerms.getUserManager().getUser(owner);
        if(user == null) {
            Bukkit.getLogger().severe("New owner doesn't exist: " + owner);
            return;
        }
        if(!user.getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build()).contains(group)) {
            user.data().add(InheritanceNode.builder(group).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
        LuckPerms luckPerms = Utils.getLuckperms();

        Group group = luckPerms.getGroupManager().getGroup(getTeamName().toLowerCase());
        if(group == null) {
            Bukkit.getLogger().severe("No group for " + getTeamName());
            return;
        }

        User user = luckPerms.getUserManager().getUser(uuid);
        if(user == null) {
            Bukkit.getLogger().severe("User with doesn't exist: " + uuid);
            return;
        }
        if(!user.getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build()).contains(group)) {
            user.data().add(InheritanceNode.builder(group).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
        LuckPerms luckPerms = Utils.getLuckperms();

        Group group = luckPerms.getGroupManager().getGroup(getTeamName().toLowerCase());
        if(group == null) {
            Bukkit.getLogger().severe("No group for " + getTeamName());
            return;
        }

        User user = luckPerms.getUserManager().getUser(uuid);
        if(user == null) {
            Bukkit.getLogger().severe("User with doesn't exist: " + uuid);
            return;
        }
        if(!user.getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build()).contains(group)) {
            user.data().remove(InheritanceNode.builder(group).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }
}
