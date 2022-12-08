package collinvht.projectr.commands.tyre;

import collinvht.projectr.commands.CommandUtil;
import collinvht.projectr.manager.tyre.TyreManager;
import collinvht.projectr.manager.tyre.Tyres;
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
                            sender.sendMessage(serverPrefix + "Item geadd");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(serverPrefix + "Ongeldig getal.");
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
