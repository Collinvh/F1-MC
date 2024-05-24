package collinvht.f1mc.module.racing.module.weather.commands.command;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.weather.manager.WeatherManager;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Forecast extends CommandUtil implements TabCompleter {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/forecast [race]", (sender, command, label, args) -> {
            if(args.length == 1) {
                return WeatherManager.getForecast(args[0]);
            } else {
                return command.getUsage();
            }
        });
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return new ArrayList<>(RaceManager.getRACES().keySet());
        } else return null;
    }
}
