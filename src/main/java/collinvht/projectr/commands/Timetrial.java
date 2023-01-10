package collinvht.projectr.commands;

import collinvht.projectr.manager.race.RacingManager;
import collinvht.projectr.manager.race.TimeTrialHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Timetrial implements CommandUtil{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
//            ItemStack stack = VehicleUtils.getItemByUUID((Player) commandSender, "F1BASE");
//            String vehicleStr = VehicleUtils.getLicensePlate(stack);
//            VehicleUtils.spawnVehicle(vehicleStr, ((Player) commandSender).getLocation());
//            VehicleUtils.enterVehicle(vehicleStr, (Player) commandSender);
//            ArmorStand mainStand = VehicleData.autostand.get("MTVEHICLES_SKIN_" + vehicleStr);

            commandSender.sendMessage(prefix + TimeTrialHandler.startSesion((Player) commandSender, RacingManager.getInstance().getRace("berlin")));
//
//            if(strings.length > 0) {
//                switch (strings[0]) {
//                    case "hide":
//                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//                            if(!onlinePlayer.getUniqueId().equals(((Player) commandSender).getUniqueId())) {
//                                onlinePlayer.hidePlayer(ProjectR.getInstance(), (Player) commandSender);
//                                onlinePlayer.hideEntity(ProjectR.getInstance(), mainStand);
//                            }
//                        }
//                        break;
//                    case "unhide":
//                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//                            if(!onlinePlayer.getUniqueId().equals(((Player) commandSender).getUniqueId())) {
//                                onlinePlayer.showPlayer(ProjectR.getInstance(), (Player) commandSender);
//                                onlinePlayer.showEntity(ProjectR.getInstance(), mainStand);
//                            }
//                        }
//                        break;
//                }
//            }
        }
        return false;
    }
}
