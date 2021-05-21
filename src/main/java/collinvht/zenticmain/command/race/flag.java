package collinvht.zenticmain.command.race;

import collinvht.zenticmain.ZenticMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class flag implements CommandExecutor {
    private final ZenticMain plugin = ZenticMain.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("zentic.fia")) {
            String currentCircuit = plugin.getConfig().getString("circuit");
            String world1 = plugin.getConfig().getString(currentCircuit + ".world");

            assert world1 != null;
            World world = Bukkit.getWorld(world1);

            int x;
            int y;
            int z;


            if (args.length > 0) {
                switch (args[0].toLowerCase(Locale.ROOT)){
                    case "geel" :
                        if (args.length > 1) {
                            switch (args[1].toLowerCase(Locale.ROOT)) {
                                case "1" :
                                    for (Player playerall : Bukkit.getOnlinePlayers()) {
                                        playerall.sendMessage(ChatColor.YELLOW + "§lYellow flag " + ChatColor.YELLOW + "S1");
                                        playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Gele vlag S1"));
                                    }

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.1.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.1.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.1.z");

                                    setBlock(x, y, z, world, Material.YELLOW_SHULKER_BOX);
                                    return true;
                                case "2" :
                                    for (Player playerall : Bukkit.getOnlinePlayers()) {
                                        playerall.sendMessage(ChatColor.YELLOW + "§lYellow flag " + ChatColor.YELLOW + "S2");
                                        playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Gele vlag S2"));
                                    }

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.2.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.2.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.2.z");

                                    setBlock(x, y, z, world, Material.YELLOW_SHULKER_BOX);
                                    return true;
                                case "3" :
                                    for (Player playerall : Bukkit.getOnlinePlayers()) {
                                        playerall.sendMessage(ChatColor.YELLOW + "§lYellow flag " + ChatColor.YELLOW + "S3");
                                        playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Gele vlag S3"));
                                    }

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.3.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.3.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.3.z");

                                    setBlock(x, y, z, world, Material.YELLOW_SHULKER_BOX);
                                    return true;
                                case "all" :
                                    for (Player playerall : Bukkit.getOnlinePlayers()) {
                                        playerall.sendMessage(ChatColor.YELLOW + "§lYellow flag " + ChatColor.YELLOW + "");
                                        playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Gele vlag"));
                                    }

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.1.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.1.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.1.z");

                                    setBlock(x, y, z, world, Material.YELLOW_SHULKER_BOX);

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.2.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.2.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.2.z");

                                    setBlock(x, y, z, world, Material.YELLOW_SHULKER_BOX);

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.3.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.3.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.3.z");

                                    setBlock(x, y, z, world, Material.YELLOW_SHULKER_BOX);
                                    return true;
                            }
                        }
                        return true;
                    case "rood" :
                        for (Player playerall : Bukkit.getOnlinePlayers()) {
                            playerall.sendMessage(ChatColor.RED + "§bRed flag " + ChatColor.RED);
                            playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Rode vlag"));
                        }

                        x = plugin.getConfig().getInt(currentCircuit + ".flags.1.x");
                        y = plugin.getConfig().getInt(currentCircuit + ".flags.1.y");
                        z = plugin.getConfig().getInt(currentCircuit + ".flags.1.z");

                        setBlock(x, y, z, world, Material.RED_SHULKER_BOX);

                        x = plugin.getConfig().getInt(currentCircuit + ".flags.2.x");
                        y = plugin.getConfig().getInt(currentCircuit + ".flags.2.y");
                        z = plugin.getConfig().getInt(currentCircuit + ".flags.2.z");

                        setBlock(x, y, z, world, Material.RED_SHULKER_BOX);

                        x = plugin.getConfig().getInt(currentCircuit + ".flags.3.x");
                        y = plugin.getConfig().getInt(currentCircuit + ".flags.3.y");
                        z = plugin.getConfig().getInt(currentCircuit + ".flags.3.z");

                        setBlock(x, y, z, world, Material.RED_SHULKER_BOX);

                        return true;
                    case "groen" :
                        if (args.length > 1) {
                            switch (args[1].toLowerCase(Locale.ROOT)) {
                                case "1" :
                                    for (Player playerall : Bukkit.getOnlinePlayers()) {
                                        playerall.sendMessage(ChatColor.GREEN + "§lGreen flag " + ChatColor.GREEN + "S1");
                                        playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Groene vlag S1"));
                                    }

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.1.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.1.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.1.z");

                                    setBlock(x, y, z, world, Material.GREEN_SHULKER_BOX);
                                    return true;
                                case "2" :
                                    for (Player playerall : Bukkit.getOnlinePlayers()) {
                                        playerall.sendMessage(ChatColor.GREEN + "§lGreen flag " + ChatColor.GREEN + "S2");
                                        playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Groene vlag S2"));
                                    }

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.2.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.2.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.2.z");

                                    setBlock(x, y, z, world, Material.GREEN_SHULKER_BOX);
                                    return true;
                                case "3" :
                                    for (Player playerall : Bukkit.getOnlinePlayers()) {
                                        playerall.sendMessage(ChatColor.GREEN + "§lGreen flag " + ChatColor.GREEN + "S3");
                                        playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Groene vlag S3"));
                                    }

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.3.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.3.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.3.z");

                                    setBlock(x, y, z, world, Material.GREEN_SHULKER_BOX);
                                    return true;
                                case "all" :
                                    for (Player playerall : Bukkit.getOnlinePlayers()) {
                                        playerall.sendMessage(ChatColor.GREEN + "§lGreen flag " + ChatColor.GREEN + "");
                                        playerall.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Groene vlag"));
                                    }

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.1.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.1.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.1.z");

                                    setBlock(x, y, z, world, Material.GREEN_SHULKER_BOX);

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.2.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.2.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.2.z");

                                    setBlock(x, y, z, world, Material.GREEN_SHULKER_BOX);

                                    x = plugin.getConfig().getInt(currentCircuit + ".flags.3.x");
                                    y = plugin.getConfig().getInt(currentCircuit + ".flags.3.y");
                                    z = plugin.getConfig().getInt(currentCircuit + ".flags.3.z");

                                    setBlock(x, y, z, world, Material.GREEN_SHULKER_BOX);
                                    return true;
                            }
                        }
                        return true;
                    case "off" :
                        x = plugin.getConfig().getInt(currentCircuit + ".flags.1.x");
                        y = plugin.getConfig().getInt(currentCircuit + ".flags.1.y");
                        z = plugin.getConfig().getInt(currentCircuit + ".flags.1.z");

                        setBlock(x, y, z, world, Material.BLACK_SHULKER_BOX);

                        x = plugin.getConfig().getInt(currentCircuit + ".flags.2.x");
                        y = plugin.getConfig().getInt(currentCircuit + ".flags.2.y");
                        z = plugin.getConfig().getInt(currentCircuit + ".flags.2.z");

                        setBlock(x, y, z, world, Material.BLACK_SHULKER_BOX);

                        x = plugin.getConfig().getInt(currentCircuit + ".flags.3.x");
                        y = plugin.getConfig().getInt(currentCircuit + ".flags.3.y");
                        z = plugin.getConfig().getInt(currentCircuit + ".flags.3.z");

                        setBlock(x, y, z, world, Material.BLACK_SHULKER_BOX);
                        return true;
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
