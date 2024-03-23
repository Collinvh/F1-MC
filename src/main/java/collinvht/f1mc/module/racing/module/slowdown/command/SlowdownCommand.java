package collinvht.f1mc.module.racing.module.slowdown.command;

import collinvht.f1mc.module.racing.module.slowdown.manager.SlowdownManager;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlowdownCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        /*
        This adds the current block in your main hand to the slowdown manager
         */
        addPart("add", 1, "/slowdown add [slowDown] {maxSpeed} {steering}", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                double maxSpeed = 1;
                if (args.length > 2) {
                    maxSpeed = Double.parseDouble(args[2]);
                }
                double steering = 1;
                if (args.length > 3) {
                    steering = Double.parseDouble(args[3]);
                }
                ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();
                double slowDown = Integer.parseInt(args[1]);
                if (stack.getType().isBlock() && stack.getType().isSolid()) {
                    return SlowdownManager.addBlock(stack, slowDown, steering, maxSpeed);
                }
                return prefix + "This block is invalid.";
            } else {
                return prefix + "You have to be a player to do this.";
            }
        }, Permissions.FIA_ADMIN, Permissions.FIA_COMMON);

        /*
        This removes the block in your main hand from the manager
         */
        addPart("remove", 0, "/slowdown remove", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();
                if (stack.getType().isBlock() && stack.getType().isSolid()) {
                    return SlowdownManager.removeBlock(stack);
                }
                return prefix + "This block is invalid.";
            } else {
                return prefix + "You have to be a player to do this.";
            }
        }, Permissions.FIA_ADMIN, Permissions.FIA_COMMON);

        /*
        This sets the max speed on the slowdown blocks, whatever happens the speed won't get below this.
         */
        addPart("speed", 1, "/slowdown speed [kmh]", (sender, command, label, args) -> {
            try {
                double speed = Double.parseDouble(args[1]);
                SlowdownManager.setMaxSpeed(speed);
                return prefix + "Max speed changed.";
            } catch (NumberFormatException e) {
                return prefix + "Invalid number.";
            }
        }, Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
    }
}
