package collinvht.zenticmain.obj;

import collinvht.zenticmain.manager.LuckPermsManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TeamObj {

    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > ";

    @Getter @Setter
    private UUID ownerUUID;

    @Getter @Setter
    private ArrayList<UUID> teamMembers = new ArrayList<>();

    private final LuckPerms api = LuckPermsManager.getApi();

    @Getter @Setter
    private ChatColor color;

    @Getter @Setter
    private String teamName;

    public TeamObj(UUID ownerUUID, String teamName, ChatColor color) {
        this.ownerUUID = ownerUUID;
        this.teamName = teamName;
        this.color = color;

        addMember(null, ownerUUID);
    }

    public void addMember(CommandSender sender, UUID uuid) {
        if(api != null) {
            Group group = api.getGroupManager().getGroup(teamName.toLowerCase());
            if(group == null) {
                try {
                    group = api.getGroupManager().createAndLoadGroup(teamName.toLowerCase()).get();

                    group.getNodes().add(PrefixNode.builder(color + teamName + " " + ChatColor.DARK_GRAY + "| ", 20).build());
                    group.getNodes().add(PermissionNode.builder("displayname.camo").permission("team.camo").build());
                } catch (InterruptedException | ExecutionException e) {
                    return;
                }
            }
            User user = api.getUserManager().getUser(uuid);
            if(user != null)
                user.data().add(InheritanceNode.builder(group).build());
        }

        teamMembers.add(uuid);
        if(sender != null) {
            sender.sendMessage(zentic + "Speler geadd!");
        }
    }

    public void removeMember(CommandSender sender, UUID uuid) {
        if(api != null) {
            Group group = api.getGroupManager().getGroup(teamName.toLowerCase());
            if(group != null) {
                User user = api.getUserManager().getUser(uuid);
                if(user != null)
                    user.data().remove(InheritanceNode.builder(group).build());
            }
        }
        teamMembers.remove(uuid);
        if(sender != null) {
            sender.sendMessage(zentic + "Speler verwijderd!");
        }
    }

    public Player getOwner() {
        return Bukkit.getPlayer(ownerUUID);
    }

    public void delete() {
        if(api != null) {
            Group group = api.getGroupManager().getGroup(teamName.toLowerCase());
            if(group != null) {
                api.getGroupManager().deleteGroup(group);
            }
        }
    }
}
