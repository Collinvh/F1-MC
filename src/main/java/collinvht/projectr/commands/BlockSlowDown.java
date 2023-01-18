package collinvht.projectr.commands;

import collinvht.projectr.util.objects.commands.CommandUtil;
import collinvht.projectr.manager.vehicle.SlowDownManager;
import collinvht.projectr.util.enums.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/*
Command /slowdown
 */
public class BlockSlowDown extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        /*
        This adds the current block in your main hand to the slowdown manager
         */
        addPart("add", 1, "/slowdown add [slowDown] {steering}", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                double steering = 1;
                if (args.length > 2) {
                    steering = Double.parseDouble(args[2]);
                }
                ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();
                double extraSpeed = Integer.parseInt(args[1]);
                if (stack.getType().isBlock() && stack.getType().isSolid()) {
                    return SlowDownManager.addBlock(stack, extraSpeed, steering);
                }
                return "This block is invalid.";
            } else {
                return "You have to be a player to do this.";
            }
        }, Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
        /*
        This removes the block in your main hand from the manager
         */
        addPart("remove", 0, "/slowdown remove", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();
                if (stack.getType().isBlock() && stack.getType().isSolid()) {
                    return SlowDownManager.removeBlock(stack);
                }
                return "This block is invalid.";
            } else {
                return "You have to be a player to do this.";
            }
        }, Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
        /*
        This sets the max speed on the slowdown blocks, whatever happens the speed won't get below this.
         */
        addPart("speed", 1, "/slowdown speed [kmh]", (sender, command, label, args) -> {
            try {
                double speed = Double.parseDouble(args[1]);
                SlowDownManager.setMaxSpeed(speed / 73.125);
                return "Max speed changed.";
            } catch (NumberFormatException e) {
                return "Invalid number.";
            }
        }, Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
    }
}
