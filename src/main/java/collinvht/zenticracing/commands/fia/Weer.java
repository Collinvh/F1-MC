package collinvht.zenticracing.commands.fia;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.racing.RaceManager;
import collinvht.zenticracing.util.objs.WeatherType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Random;

public class Weer implements CommandUtil {

    @Getter
    private static WeatherType type = WeatherType.OFF;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("zentic.fia")) {
                if(args.length > 1) {
                    if(args[0].equalsIgnoreCase("set")) {
                        for (WeatherType value : WeatherType.values()) {
                            if (value.getNaam().equalsIgnoreCase(args[1])) {
                                type = value;
                                startType((Player) sender);
                                return true;
                            }

                            if (value.getNaam().toLowerCase().contains(args[1].toLowerCase())) {
                                type = value;
                                startType((Player) sender);
                                return true;
                            }
                        }
                        sender.sendMessage(prefix + "Dat weerstype herkennen wij niet.");
                        return true;
                    } else if(args[1].equalsIgnoreCase("reset")) {
                        type = WeatherType.OFF;
                        sender.sendMessage(prefix + "Weer gereset.");
                        startType((Player) sender);
                        return true;
                    }
                } else {
                    sendUsage(sender, "/weer ");
                }
            } else {
                sender.sendMessage(prefix + "Geen permissie.");
            }
        }
        return true;
    }


    private void startType(Player sender) {
        World world;
        if(RaceManager.getRunningRace() != null) {
            world = RaceManager.getRunningRace().getStorage().getS3().getWorld();
        } else {
            world = sender.getWorld();
        }

        if(world != null) {
            switch (type) {
                case OFF:
                    world.setThundering(false);
                    world.setStorm(false);
                    world.setWeatherDuration(0);
                    break;
                case ZACHT:
                case NORMAAL:
                    world.setThundering(false);
                    world.setStorm(true);
                    break;
                case HEFTIG:
                case STORM:
                    world.setStorm(true);
                    world.setThundering(true);
                    break;
            }
        }
    }
}
