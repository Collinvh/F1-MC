package collinvht.zenticracing.commands;

import collinvht.zenticracing.commands.racing.computer.ers.ERSComputer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestPc implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("zentic.test")) {
            if(sender instanceof Player) {
                ERSComputer.openInventory(((Player) sender).getPlayer());
            }
        }
        return true;
    }
}
