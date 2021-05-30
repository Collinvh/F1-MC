package collinvht.zenticracing.commands.racing;

import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class StandCommand implements CommandExecutor {
    public static final String prefix = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            HashMap<UUID, DriverObject> drivers = DriverManager.getDrivers();
            LinkedHashMap<DriverObject, Integer> sectors = new LinkedHashMap<>();

            drivers.forEach((unused, driver) -> sectors.put(driver, driver.getLapstorage().getSectors()));

            LinkedHashMap<DriverObject, Integer> treeMap = DriverManager.sortByValueDesc(sectors);

            if(treeMap.size() > 0) {
                sender.sendMessage(prefix + " Sector Stand");

                AtomicInteger pos = new AtomicInteger();
                treeMap.forEach((driver, integer) -> {
                    if(integer > 0) {
                        pos.getAndIncrement();
                        sender.sendMessage(pos.get() + ". " + driver.getPlayer().getDisplayName() + " : " + integer);
                    }
                });
            } else {
                sender.sendMessage(prefix + " Er is nog niemand aan het rijden!");
            }
            return true;
        }
        return false;
    }

}
