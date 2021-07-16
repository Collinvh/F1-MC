package collinvht.zenticracing.commands.fia;

import collinvht.zenticracing.ZenticRacing;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

public class Flag implements CommandExecutor {
    private final ZenticRacing plugin = ZenticRacing.getRacing();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("zentic.fia")) {

            int x;
            int y;
            int z;

            String world1 = plugin.getConfig().getString(args[0] + ".world");

            assert world1 != null;
            World world = Bukkit.getWorld(world1);


            if (args.length > 1) {
                switch (args[1].toLowerCase(Locale.ROOT)){
                    case "geel" :

                        if (args.length > 2) {
                            int sector = Integer.parseInt(args[2]);

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.YELLOW + "YELLOW FLAG S" + sector);
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, UUID.fromString(ChatColor.YELLOW + "YELLOW FLAG S" + sector));
                            }

                            x = plugin.getConfig().getInt(args[0] + ".flags." + sector + ".x");
                            y = plugin.getConfig().getInt(args[0] + ".flags." + sector + ".y");
                            z = plugin.getConfig().getInt(args[0] + ".flags." + sector + ".z");

                            setBlock(x, y, z, world, Material.YELLOW_SHULKER_BOX);
                        }

                        return true;
                    //
                }
            }
        }else {
            sender.sendMessage(ChatColor.RED + "Je hebt geen permissie hiervoor");
        }

        return false;
    }

    private Runnable setBlock(int x, int y, int z, World world, Material material) {
        Location location = new Location(world, x, y, z);
        location.getBlock().setType(material);
        return null;
    }
}
