package collinvht.f1mc.module.main.command.commands;

import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CountryCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("add", 3, "/country add [name] [short] [image]", ((sender, command, label, args) -> prefix + CountryManager.addCountry(args[1], args[2], args[3])), Permissions.FIA_ADMIN);
        addPart("%", 0, "/country [countryname]", ((sender, command, label, args) -> {
            if(sender instanceof Player) {
                return prefix + CountryManager.updateCountry((Player) sender, args[0]);
            } else {
                return prefix + "Only a player can do that.";
            }
        }));
    }
}
