package collinvht.f1mc.module.racing.module.team.object;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.team.manager.TeamManager;
import collinvht.f1mc.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Getter
public class TeamObj {
    private String teamName;
    @Setter
    private String groupName;
    private String teamPrefix;

    private UUID owner = null;
    private ChatColor teamColor = ChatColor.WHITE;

    @Setter
    private ArrayList<UUID> members = new ArrayList<>();
    @Setter
    private ArrayList<UUID> requests = new ArrayList<>();

    public TeamObj(String name) {
        this.teamName = name;
        this.groupName = name.replace(" ", "_").toLowerCase();
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
                } else {
                    user.data().remove(InheritanceNode.builder(group).build());
                    luckPerms.getUserManager().saveUser(user);
                }
            }
            owner = null;
        }
        ArrayList<UUID> allMembers = new ArrayList<>(members);
        allMembers.forEach(this::removeMember);

        Group teamGroup = luckPerms.getGroupManager().getGroup(teamName);
        if(teamGroup != null) luckPerms.getGroupManager().deleteGroup(teamGroup);

        File path = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/teams/" + teamName + ".json").toFile();
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
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if(group != null) {
            group.getData(DataType.NORMAL).clear();
            group.getData(DataType.NORMAL).add(PrefixNode.builder().prefix( ChatColor.DARK_GRAY + "|" + getTeamColor() + getTeamPrefix() + ChatColor.DARK_GRAY +"| " + ChatColor.RESET).withContext("server", "racing").priority(10).build());
            group.getData(DataType.NORMAL).add(PermissionNode.builder("blocklocker.group." + getTeamName()).permission("team." + getTeamName()).withContext("server", "racing").build());
            luckPerms.getGroupManager().saveGroup(group);
        } else {
            TeamManager.createTeam(this);
        }
    }

    public static TeamObj fromJson(JsonObject json) {
        try {
            String name = json.get("Name").getAsString();
            String groupname = json.get("Groupname").getAsString();
            String prefix = json.get("Prefix").getAsString();
            String color = json.get("TeamColor").getAsString();

            JsonArray requestArray = json.getAsJsonArray("requests");
            JsonArray memberArray = json.getAsJsonArray("members");


            TeamObj team = new TeamObj(name);
            team.setTeamColor(ChatColor.getByChar(color.charAt(1)));
            team.setGroupName(groupname);
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
        File path = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/teams/").toFile();

        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("Name", teamName);
        mainObject.addProperty("Groupname", groupName);
        mainObject.addProperty("Prefix", teamPrefix);
        mainObject.addProperty("TeamColor", teamColor.toString());
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
            user.data().remove(InheritanceNode.builder(group).build());
            luckPerms.getUserManager().saveUser(user);
        }
        owner = uuid;

        User user = luckPerms.getUserManager().getUser(owner);
        if(user == null) {
            Bukkit.getLogger().severe("New owner doesn't exist: " + owner);
            return;
        }
        user.data().add(InheritanceNode.builder(group).build());
        luckPerms.getUserManager().saveUser(user);
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
        LuckPerms luckPerms = Utils.getLuckperms();

        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if(group == null) {
            Bukkit.getLogger().severe("No group for " + getTeamName());
            return;
        }

        User user = luckPerms.getUserManager().getUser(uuid);
        if(user == null) {
            Bukkit.getLogger().severe("User with doesn't exist: " + uuid);
            return;
        }
        user.data().add(InheritanceNode.builder(group).build());
        luckPerms.getUserManager().saveUser(user);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
        LuckPerms luckPerms = Utils.getLuckperms();

        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if(group == null) {
            Bukkit.getLogger().severe("No group for " + getTeamName());
            return;
        }

        User user = luckPerms.getUserManager().getUser(uuid);
        if(user == null) {
            Bukkit.getLogger().severe("User with doesn't exist: " + uuid);
            return;
        }
        user.data().remove(InheritanceNode.builder(group).build());
        luckPerms.getUserManager().saveUser(user);
    }
}