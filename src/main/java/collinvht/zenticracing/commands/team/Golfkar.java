package collinvht.zenticracing.commands.team;

import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.team.object.TeamObject;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.api.objects.spawn.SpawnMode;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.StorageVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Golfkar implements CommandUtil {

    private static final Location loc1 = new Location(Bukkit.getWorld("spawn"), -12, 73, -102, -90 ,0);
    private static final Location loc2 = new Location(Bukkit.getWorld("spawn"), 265, 73, -254, 90, 0);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (player != null) {
                if (player.getWorld() == loc1.getWorld()) {
                    BaseVehicle kart = VehiclesPlusAPI.getVehicleManager().getBaseVehicleMap().get("Golfkar");

                    TeamObject team = Team.checkTeamForPlayer(player);
                    if (team != null) {
                        if (args.length > 0) {
                            switch (args[0].toLowerCase()) {
                                case "verwijderen":
                                case "delete":
                                case "remove":
                                case "despawn":
                                    if (team.getGolfKart() != null) {
                                        team.getGolfKart().despawn(true);
                                        team.getGolfKart().getStorageVehicle().removeVehicle(player);
                                    }
                                    sender.sendMessage(prefix + "Golfkart gedespawned.");
                                    return true;
                            }
                        } else {
                            if (team.getGolfcooldown() <= 0) {
                                if (sender.hasPermission("zentic.spawngolf")) {
                                    if (team.getGolfKart() != null) {
                                        team.getGolfKart().despawn(true);
                                    }

                                    StorageVehicle vehicle = VehiclesPlusAPI.getInstance().createVehicle(kart, ((Player) sender).getPlayer());
                                    if (vehicle != null) {
                                        Location spawnLoc = loc1;
                                        if (player.getLocation().distance(loc1) > player.getLocation().distance(loc2)) {
                                            spawnLoc = loc2;
                                        }

                                        SpawnedVehicle v = vehicle.spawnVehicle(spawnLoc, SpawnMode.GARAGE);
                                        team.setGolfKart(v);

                                        sender.sendMessage(prefix + "Golfkart gespawned.");

                                        team.setGolfcooldown(300);
                                    } else {
                                        Bukkit.getLogger().warning("Vehicle null");
                                    }
                                } else {
                                    sender.sendMessage(prefix + "Geen permissie.");
                                }
                            } else {
                                sender.sendMessage(prefix + "Golfkart is op cooldown.");
                            }
                        }
                    }
                }
            } else {
                sender.sendMessage(prefix + "Je zit niet in een team?");
            }
            return true;
        }
        return false;
    }
}
