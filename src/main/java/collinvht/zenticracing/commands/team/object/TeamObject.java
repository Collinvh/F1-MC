package collinvht.zenticracing.commands.team.object;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.util.objs.LuckPermsUtil;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TeamObject {

    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;

    @Getter @Setter
    private ArrayList<UUID> ownerUUID;

    @Getter @Setter
    private ArrayList<UUID> teamMembers = new ArrayList<>();

    @Getter @Setter
    private ArrayList<UUID> invited = new ArrayList<>();

    @Getter @Setter
    private Bestelling bestelling;

    @Getter
    private final boolean heeftBesteld = bestelling == null;

    @Getter
    private final ArrayList<RaceCar> raceCars = new ArrayList<>();

    @Getter @Setter
    private SpawnedVehicle golfKart;

    @Getter @Setter
    private int golfcooldown;

    private final LuckPerms api = LuckPermsUtil.getLuckPerms();

    @Getter
    private ChatColor color;

    @Getter @Setter
    private String teamName;

    public TeamObject(ArrayList<UUID> ownerUUID, String teamName, ChatColor color) {
        this.ownerUUID = ownerUUID;

        this.teamName = teamName;
        this.color = color;

        addMember(null, ownerUUID.get(0));
    }

    public void inviteMember(CommandSender sender, UUID uuid) {
        if(invited.contains(uuid)) {
            sender.sendMessage(zentic + "Die speler heb jij al geinvite!");
        } else {
            invited.add(uuid);
            sender.sendMessage(zentic + "Speler geinvite!");

            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                player.sendMessage(zentic + getTeamName() + ", heeft je uitgenodigd om hun team te joinen!");
            }
        }
    }

    public void setTeamName(String teamName) {
        if(api != null) {
            Group group = api.getGroupManager().getGroup(teamName.toLowerCase());

            if(group != null) {
                group.getData(DataType.NORMAL).remove(PrefixNode.builder(color + this.teamName + " " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY, 20).build());
                group.getData(DataType.NORMAL).add(PrefixNode.builder(color + teamName + " " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY, 20).build());
                api.getGroupManager().saveGroup(group);
            }
        }

        this.teamName = teamName;
    }

    public void setColor(ChatColor color) {
        if(api != null) {
            Group group = api.getGroupManager().getGroup(teamName.toLowerCase());

            if(group != null) {
                group.getData(DataType.NORMAL).remove(PrefixNode.builder(this.color + teamName + " " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY, 20).build());
                group.getData(DataType.NORMAL).add(PrefixNode.builder(color + teamName + " " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY, 20).build());
                api.getGroupManager().saveGroup(group);
            }
        }
        this.color = color;
    }

    public void addMember(CommandSender sender, UUID uuid) {
        if(api != null) {
            Group group = api.getGroupManager().getGroup(teamName.toLowerCase());
            if(group == null) {
                try {
                    group = api.getGroupManager().createAndLoadGroup(teamName.toLowerCase()).get();

                    group.getData(DataType.NORMAL).add(PrefixNode.builder(color + teamName + " " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY, 20).build());
                    group.getData(DataType.NORMAL).add(PermissionNode.builder("displayname." + teamName.toLowerCase()).permission("team." + teamName.toLowerCase()).build());

                    api.getGroupManager().saveGroup(group);
                } catch (InterruptedException | ExecutionException e) {
                    return;
                }
            }
            User user = api.getUserManager().getUser(uuid);
            if(user != null) {
                user.data().add(InheritanceNode.builder(group).build());
                api.getUserManager().saveUser(user);
            }

        }


        invited.remove(uuid);
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
                if(user != null) {
                    user.data().remove(InheritanceNode.builder(group).build());
                    api.getUserManager().saveUser(user);
                }
            }
        }
        teamMembers.remove(uuid);
        if(sender != null) {
            sender.sendMessage(zentic + "Speler verwijderd!");
        }
    }

    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(ownerUUID.get(0));
    }

    public void setGolfcooldown(int cooldown) {
        this.golfcooldown = cooldown;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(golfcooldown <= 0) {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                } else {
                    golfcooldown--;
                }
            }
        }.runTaskTimer(ZenticRacing.getRacing(), 0, 15);
    }

    public void delete() {
        if(api != null) {
            Group group = api.getGroupManager().getGroup(teamName.toLowerCase());
            try {
                teamMembers.forEach(uuid -> removeMember(null, uuid));
            } catch (ConcurrentModificationException ignored) {
            }

            if(group != null) {
                api.getGroupManager().deleteGroup(group);
                api.getGroupManager().saveGroup(group);
            }
        }
    }

    public RaceCar getRaceCarFromVehicle(SpawnedVehicle vehicle) {
        for (RaceCar raceCar : raceCars) {
            if(raceCar.getSpawnedVehicle().getStorageVehicle().getUuid().equals(vehicle.getStorageVehicle().getUuid())) {
                return raceCar;
            }
        }
        return null;
    }

    public void addRaceCar(RaceCar car) {
        this.raceCars.add(car);
    }
}
