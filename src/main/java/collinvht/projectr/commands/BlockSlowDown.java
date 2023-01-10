package collinvht.projectr.commands;

import collinvht.projectr.commands.commandusage.UsageBuilder;
import collinvht.projectr.manager.vehicle.SlowDownManager;
import collinvht.projectr.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockSlowDown implements CommandUtil {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (Permissions.FIA_ADMIN.hasPermission(commandSender) || Permissions.FIA_COMMON.hasPermission(commandSender)) {
            if (args.length > 0) {
                if (commandSender instanceof Player) {
                    if (args[0].equals("add")) {
                        if (args.length > 1) {
                            try {
                                double steering = 1;
                                if (args.length > 2) {
                                    steering = Double.parseDouble(args[2]);
                                }
                                ItemStack stack = ((Player) commandSender).getInventory().getItemInMainHand();
                                double extraSpeed = Integer.parseInt(args[1]);
                                if (stack.getType().isBlock() && stack.getType().isSolid()) {
                                    commandSender.sendMessage(prefix + SlowDownManager.addBlock(stack, extraSpeed, steering));
                                    return true;
                                }
                                commandSender.sendMessage(prefix + "Je moet wel een geldig block vasthouden.");
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage(prefix + "Ongeldig getal.");
                                return false;
                            }
                            return true;
                        } else {
                            commandSender.sendMessage(prefix + "/blockslowdown add [slowDownSpeed] {steering}");
                        }
                    } else if (args[0].equals("remove")) {
                        ItemStack stack = ((Player) commandSender).getInventory().getItemInMainHand();
                        if (stack.getType().isBlock() && stack.getType().isSolid()) {
                            commandSender.sendMessage(prefix + SlowDownManager.removeBlock(stack));
                            return true;
                        }
                        commandSender.sendMessage(prefix + "Je moet wel een geldig block vasthouden.");
                        return true;
                    } else {
                        commandSender.sendMessage(prefix + "Dit is geen geldig argument");
                    }
                    return true;
                }
            } else {
                UsageBuilder builder = new UsageBuilder();
                builder.addUsage("/blockslowdown add [slowDownSpeed] {steering}", Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
                builder.addUsage("/blockslowdown remove", Permissions.FIA_ADMIN, Permissions.FIA_COMMON);
                commandSender.sendMessage(prefix + "Command Usage:\n" + builder.buildUsages(commandSender));
                return false;
            }
        }
        return false;
    }
}
