package collinvht.f1mc.module.racing.module.slowdown.command;

import collinvht.f1mc.module.racing.module.slowdown.manager.SlowdownManager;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SlowdownCommand extends CommandUtil implements TabCompleter {
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


                double slowDown = Double.parseDouble(args[1]);
                if (stack.getType().isBlock() && stack.getType().isSolid()) {
                    return prefix +     SlowdownManager.addBlock(stack, slowDown, steering, maxSpeed);
                } else {
                    CustomBlock block = CustomBlock.byItemStack(stack);
                    if(block != null) {
                        sender.sendMessage(block.getNamespacedID());
                        return prefix + SlowdownManager.addCustomBlock(block.getNamespacedID(), slowDown, steering, maxSpeed);
                    }
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
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1) {
            ArrayList<String> list = new ArrayList<>();
            list.add("add");
            list.add("remove");
            return list;
        }
        return null;
    }
}
