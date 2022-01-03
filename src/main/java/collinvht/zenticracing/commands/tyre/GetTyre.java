package collinvht.zenticracing.commands.tyre;

import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.manager.tyre.TyreManager;
import collinvht.zenticracing.manager.tyre.Tyres;
import collinvht.zenticracing.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GetTyre implements CommandUtil {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("fia")) {
            if(sender instanceof Player) {
                if(args.length > 0) {
                    try {
                        int id = Integer.parseInt(args[0]);
                        Tyres tyre = Tyres.getTyreFromID(id);

                        if (tyre != null) {
                            ItemStack stack = TyreManager.getTyre(tyre);
                            Player player = (Player) sender;
                            player.getInventory().addItem(stack);
                            sender.sendMessage(prefix + "Item geadd");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(prefix + "Ongeldig getal.");
                    }
                } else {
                    sendUsage(sender, "/gettyre [id]");
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
