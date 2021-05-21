package collinvht.zenticmain.command.util;

import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.api.objects.spawn.SpawnMode;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.StorageVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class SpawnCar implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(args.length > 0) {
                String arg0 = args[0];
                if(arg0.equalsIgnoreCase("clear")) {
                    VehiclesPlusAPI.getVehicleManager().getPlayerVehicleHashMap().get(((Player) sender).getPlayer()).clear();
                } else {
                    BaseVehicle kart = VehiclesPlusAPI.getVehicleManager().getBaseVehicleMap().get(args[0]);
                    if (kart != null) {
                        StorageVehicle vehicle = VehiclesPlusAPI.getInstance().createVehicle(kart, ((Player) sender).getPlayer());
                        if (sender.hasPermission("zentic.fia")) {
                            vehicle.spawnVehicle(((Player) sender).getLocation(), SpawnMode.FORCE);
                        } else {

                        }
                    }
                }
            }
        }

        return false;
    }
}
