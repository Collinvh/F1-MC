package collinvht.zenticracing.commands.racing;

import collinvht.zenticracing.commands.racing.laptime.object.Laptime;
import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PersoonlijkRecord implements CommandExecutor {
    public static final String prefix = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            DriverObject driver = DriverManager.getDrivers().get(((Player) sender).getUniqueId());
            if(driver != null) {
                ArrayList<Laptime> list = driver.getLapstorage().getLaptimes();
                if(list.size() > 0) {
                    sender.sendMessage(prefix + " Jouw laatste tien laps! :");
                    list.forEach(laptimeOBJ -> sender.sendMessage("" + ChatColor.BOLD + ChatColor.GREEN + Laptime.millisToTimeString(laptimeOBJ.getLaptime()) + " | " + ChatColor.RESET + Laptime.millisToTimeString(laptimeOBJ.getS1data().getSectorLength()) + "/" +  Laptime.millisToTimeString(laptimeOBJ.getS2data().getSectorLength()) +  "/" + Laptime.millisToTimeString(laptimeOBJ.getS3data().getSectorLength()) + "/"));
                } else {
                    sender.sendMessage(prefix + " Je hebt nog geen laps gereden!");
                }
            }
        }

        return false;
    }
}
