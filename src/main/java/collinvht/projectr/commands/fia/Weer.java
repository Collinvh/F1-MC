package collinvht.projectr.commands.fia;

import collinvht.projectr.commands.CommandUtil;
import collinvht.projectr.commands.racing.RaceManager;
import collinvht.projectr.util.objs.WeatherType;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Weer implements CommandUtil {

    @Getter
    private static WeatherType type = WeatherType.OFF;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("projectr.fia")) {
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
                        sender.sendMessage(serverPrefix + "Dat weerstype herkennen wij niet.");
                        return true;
                    } else if(args[1].equalsIgnoreCase("reset")) {
                        type = WeatherType.OFF;
                        sender.sendMessage(serverPrefix + "Weer gereset.");
                        startType((Player) sender);
                        return true;
                    }
                } else {
                    sendUsage(sender, "/weer ");
                }
            } else {
                sender.sendMessage(serverPrefix + "Geen permissie.");
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
