package collinvht.f1mc.module.main.command.commands;

import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.module.main.gui.CountryGUIs;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.window.Window;

import java.util.*;

public class CountryCommand extends CommandUtil implements TabCompleter {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "", ((sender, command, label, args) -> {
            if(sender instanceof Player) {
                CountryGUIs.getCountryGUI(((Player) sender).getPlayer());
                return prefix + "Opened GUI.";
            } else {
                return prefix + "Only a player can do that.";
            }
        }));

        addPart("set", 1, "/country set [country]", ((sender, command, label, args) -> {
            if(sender instanceof Player) {
                return prefix + CountryManager.updateCountry(((Player) sender).getPlayer(), args[1].toLowerCase());
            } else {
                return prefix + "Only a player can do that";
            }
        }));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length > 1) {
            if(strings[0].equalsIgnoreCase("set")) {
                LinkedList<String> countries = new LinkedList<>();
                CountryManager.getCountries().forEach((string, countryObject) -> {
                    countries.add(string.toLowerCase());
                });
                countries.sort(Comparator.comparing(o -> o));
                return countries;
            }
        }
        return null;
    }
}
