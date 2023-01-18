package collinvht.projectr.commands;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.objects.commands.CommandUtil;
import ia.m.H;
import lombok.Getter;
import nl.mtvehicles.core.infrastructure.models.VehicleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class TukTukCommand extends CommandUtil {
    @Getter
    private static final HashMap<UUID, String> tuktuks = new HashMap<>();
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("spawn", 1, "/tuktuk spawn [player]", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                return "Only console is allowed to run this command.";
            } else {
                Player player = Bukkit.getPlayer(args[1]);
                if(player == null) return "Player is invalid";
                World world = Bukkit.getWorld("gb");
                if(world == null) return "World GB is not loaded.";
                ItemStack stack = VehicleUtils.getItemByUUID(player, "TUK_TUK");
                if(stack != null) {
                    String plate = VehicleUtils.getLicensePlate(stack);
                    //Hardcoded for now
                    Location location = new Location(Bukkit.getWorld("gb"), 278, 59, 241, 90, 0);
                    VehicleUtils.spawnVehicle(plate, location);
                    player.teleport(location);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            VehicleUtils.enterVehicle(plate, player);
                            tuktuks.put(player.getUniqueId(), plate);
                        }
                    }.runTaskLater(ProjectR.getInstance(), 1);
                } else {
                    return "Tuk tuk is an invalid vehicle.";
                }
                return "";
            }
        });
    }
}
