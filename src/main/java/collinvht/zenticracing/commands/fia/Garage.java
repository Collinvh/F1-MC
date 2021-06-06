package collinvht.zenticracing.commands.fia;

import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.object.TeamObject;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.api.objects.spawn.SpawnMode;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.StorageVehicle;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Garage implements CommandUtil {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0) {
                String arg0 = args[0];
                if (arg0.equalsIgnoreCase("clear")) {
                    if(args.length > 1) {
                        if (args[1].equalsIgnoreCase("all")) {
                            VehiclesPlusAPI.getVehicleManager().getPlayerVehicleHashMap().forEach((player, storageVehicles) -> VehiclesPlusAPI.getVehicleManager().getPlayerVehicleHashMap().put(player, new ArrayList<>())
                            );
                        }
                    } else {
                        VehiclesPlusAPI.getVehicleManager().getPlayerVehicleHashMap().put(((Player) sender).getPlayer(), new ArrayList<>());
                    }
                } else {
                    BaseVehicle kart = VehiclesPlusAPI.getVehicleManager().getBaseVehicleMap().get(args[0]);
                    if (kart != null) {
                        StorageVehicle vehicle = VehiclesPlusAPI.getInstance().createVehicle(kart, ((Player) sender).getPlayer());
                        if (sender.hasPermission("zentic.fia")) {
                            SpawnedVehicle vehicle1 = vehicle.spawnVehicle(((Player) sender).getLocation(), SpawnMode.FORCE);
                            if (args.length > 1) {
                                TeamObject object = Team.getTeamObj().get(args[1].toLowerCase());
                                if (object != null) {
                                    object.addRaceCar(new RaceCar(vehicle1));
                                } else {
                                    sender.sendMessage(prefix + "Dat team bestaat niet!");
                                    return false;
                                }
                            }
                            sender.sendMessage(prefix + "Auto gespawned");
                        }
                    }
                }
            }
        }

        return false;
    }
}
