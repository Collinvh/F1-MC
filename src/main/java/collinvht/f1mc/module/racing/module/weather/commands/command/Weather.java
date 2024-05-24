package collinvht.f1mc.module.racing.module.weather.commands.command;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.weather.commands.WeatherCommands;
import collinvht.f1mc.module.racing.module.weather.manager.WeatherManager;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTransitionSpeed;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTypes;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Weather extends CommandUtil implements TabCompleter {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("set", 2, "/weather set [race] {type} {transition} {delay}", (sender, command, label, args) -> {
            if(args.length == 3) {
                return WeatherManager.setWeather(args[1], args[2]);
            } else if(args.length == 4) {
                return WeatherManager.setWeather(args[1], args[2], args[3]);
            } else if(args.length == 5) {
                long delay = 0;
                try {
                    delay = Long.parseLong(args[4]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(prefix + "That delay is invalid, it's set to 0 by default");
                }
                return WeatherManager.setWeather(delay, args[1], args[2], args[3]);
            } else {
                return WeatherManager.setWeather(args[1]);
            }
        }, Permissions.FIA_ADMIN);
        addPart("get", 1, "/weather get [race]", ((sender, command, label, args) -> WeatherManager.getWeather(args[1])), Permissions.FIA_ADMIN);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 2) {
            return new ArrayList<>(RaceManager.getRACES().keySet());
        }
        if(args.length == 3) {
            ArrayList<String> list = new ArrayList<>();
            for (WeatherTypes value : WeatherTypes.values()) {
                list.add(value.getName());
            }
            return list;
        }
        if(args.length == 4) {
            ArrayList<String> list = new ArrayList<>();
            for (WeatherTransitionSpeed value : WeatherTransitionSpeed.values()) {
                list.add(value.getName());
            }
            return list;
        } else if(args.length == 1) {
            ArrayList<String> list = new ArrayList<>();
            list.add("get");
            list.add("set");
            return list;
        }
        return null;
    }
}
