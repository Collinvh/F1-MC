package collinvht.f1mc.module.main.command.commands;

import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.module.main.gui.CountryGUIs;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.window.Window;

public class CountryCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "", ((sender, command, label, args) -> {
            if(sender instanceof Player) {
                Window.single().setTitle(ChatColor.GRAY + "Select your country!").setGui(CountryGUIs.getCountryGUI()).build((Player) sender).open();
                return prefix + "Opened GUI.";
            } else {
                return prefix + "Only a player can do that.";
            }
        }));
    }
}
