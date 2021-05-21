package collinvht.zenticmain.command.race;

import collinvht.zenticmain.ZenticMain;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class lights implements CommandExecutor {

    private final ZenticMain plugin = ZenticMain.getInstance();

    private final String currentCircuit = plugin.getConfig().getString("circuit");

    private final int y1 = plugin.getConfig().getInt(currentCircuit + ".grid.y1");
    private final int y2 = plugin.getConfig().getInt(currentCircuit + ".grid.y2");

    private final int grid_1x = plugin.getConfig().getInt(currentCircuit + ".grid.1.x");
    private final int grid_2x = plugin.getConfig().getInt(currentCircuit + ".grid.2.x");
    private final int grid_3x = plugin.getConfig().getInt(currentCircuit + ".grid.3.x");
    private final int grid_4x = plugin.getConfig().getInt(currentCircuit + ".grid.4.x");
    private final int grid_5x = plugin.getConfig().getInt(currentCircuit + ".grid.5.x");

    private final int grid_1z = plugin.getConfig().getInt(currentCircuit + ".grid.1.z");
    private final int grid_2z = plugin.getConfig().getInt(currentCircuit + ".grid.2.z");
    private final int grid_3z = plugin.getConfig().getInt(currentCircuit + ".grid.3.z");
    private final int grid_4z = plugin.getConfig().getInt(currentCircuit + ".grid.4.z");
    private final int grid_5z = plugin.getConfig().getInt(currentCircuit + ".grid.5.z");

    private final int supporty = plugin.getConfig().getInt(currentCircuit + ".grid-support.y");

    private final int supportx1 = plugin.getConfig().getInt(currentCircuit + ".grid-support.1.x");
    private final int supportx2 = plugin.getConfig().getInt(currentCircuit + ".grid-support.2.x");
    private final int supportx3 = plugin.getConfig().getInt(currentCircuit + ".grid-support.3.x");

    private final int supportz1 = plugin.getConfig().getInt(currentCircuit + ".grid-support.1.z");
    private final int supportz2 = plugin.getConfig().getInt(currentCircuit + ".grid-support.2.z");
    private final int supportz3 = plugin.getConfig().getInt(currentCircuit + ".grid-support.3.z");

    private final int pitentryx = plugin.getConfig().getInt(currentCircuit + ".pit.entry.x");
    private final int pitentryy = plugin.getConfig().getInt(currentCircuit + ".pit.entry.y");
    private final int pitentryz = plugin.getConfig().getInt(currentCircuit + ".pit.entry.z");

    private final int pitexitx = plugin.getConfig().getInt(currentCircuit + ".pit.exit.x");
    private final int pitexity = plugin.getConfig().getInt(currentCircuit + ".pit.exit.y");
    private final int pitexitz = plugin.getConfig().getInt(currentCircuit + ".pit.exit.z");

    private int time;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("zentic.fia")) {

            String world1 = plugin.getConfig().getString(currentCircuit + ".world");

            assert world1 != null;
            World world = Bukkit.getWorld(world1);

            if (args.length > 1) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "start" :

                        int max = 60;
                        int min = 10;
                        int range = max - min + 1;

                        // generate random numbers within 1 to 10
                        for (int i = 0; i < 10; i++) {
                            int rand = (int) (Math.random() * range) + min;

                            // Output is different everytime this code is executed
                            this.time = rand;
                        }

                        this.time = time + 60;

                        runLights(world, grid_1x, y1, grid_1z, Material.RED_SHULKER_BOX);
                        runLights(world, grid_1x, y2, grid_1z, Material.RED_SHULKER_BOX);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_1x, y1, grid_1z, Material.BLACK_SHULKER_BOX), time);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_1x, y2, grid_1z, Material.BLACK_SHULKER_BOX), time);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y1, grid_2z, Material.RED_SHULKER_BOX), 15L);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y2, grid_2z, Material.RED_SHULKER_BOX), 15L);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y1, grid_2z, Material.BLACK_SHULKER_BOX), time);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y2, grid_2z, Material.BLACK_SHULKER_BOX), time);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y1, grid_3z, Material.RED_SHULKER_BOX), 30L);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y2, grid_3z, Material.RED_SHULKER_BOX), 30L);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y1, grid_3z, Material.BLACK_SHULKER_BOX), time);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y2, grid_3z, Material.BLACK_SHULKER_BOX), time);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y1, grid_4z, Material.RED_SHULKER_BOX), 45L);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y2, grid_4z, Material.RED_SHULKER_BOX), 45L);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y1, grid_4z, Material.BLACK_SHULKER_BOX), time);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y2, grid_4z, Material.BLACK_SHULKER_BOX), time);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y1, grid_5z, Material.RED_SHULKER_BOX), 60L);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y2, grid_5z, Material.RED_SHULKER_BOX), 60L);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y1, grid_5z, Material.BLACK_SHULKER_BOX), time);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y2, grid_5z, Material.BLACK_SHULKER_BOX), time);

                        runLights(world, supportx1, supporty, supportz1, Material.RED_SHULKER_BOX);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, supportx1, supporty, supportz1, Material.BLACK_SHULKER_BOX), time);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, supportx2, supporty, supportz2, Material.BLACK_SHULKER_BOX), time);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, supportx2, supporty, supportz2, Material.RED_SHULKER_BOX), 30L);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, supportx3, supporty, supportz3, Material.BLACK_SHULKER_BOX), time);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> runLights(world, supportx3, supporty, supportz3, Material.RED_SHULKER_BOX), 60L);

                        return true;
                    //
                    case "extraformation" :
                        runLights(world, grid_1x, y1, grid_1z, Material.ORANGE_SHULKER_BOX);
                        runLights(world, grid_1x, y2, grid_1z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_2x, y1, grid_2z, Material.ORANGE_SHULKER_BOX);
                        runLights(world, grid_2x, y2, grid_2z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_3x, y1, grid_3z, Material.ORANGE_SHULKER_BOX);
                        runLights(world, grid_3x, y2, grid_3z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_4x, y1, grid_4z, Material.ORANGE_SHULKER_BOX);
                        runLights(world, grid_4x, y2, grid_4z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_5x, y1, grid_5z, Material.ORANGE_SHULKER_BOX);
                        runLights(world, grid_5x, y2, grid_5z, Material.BLACK_SHULKER_BOX);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_1x, y1, grid_1z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_1x, y2, grid_1z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y1, grid_2z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y2, grid_2z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y1, grid_3z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y2, grid_3z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y1, grid_4z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y2, grid_4z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y1, grid_5z, Material.BLACK_SHULKER_BOX), 15L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y2, grid_5z, Material.BLACK_SHULKER_BOX), 15L);

                        Bukkit.getScheduler().runTaskLater(plugin,() -> runLights(world, grid_2x, y1, grid_2z, Material.GREEN_SHULKER_BOX), 60L);
                        Bukkit.getScheduler().runTaskLater(plugin,() -> runLights(world, grid_4x, y1, grid_4z, Material.GREEN_SHULKER_BOX), 60L);
                        Bukkit.getScheduler().runTaskLater(plugin,() -> runLights(world, grid_1x, y1, grid_1z, Material.ORANGE_SHULKER_BOX), 75L);
                        Bukkit.getScheduler().runTaskLater(plugin,() -> runLights(world, grid_3x, y1, grid_3z, Material.ORANGE_SHULKER_BOX), 75L);
                        Bukkit.getScheduler().runTaskLater(plugin,() -> runLights(world, grid_5x, y1, grid_5z, Material.ORANGE_SHULKER_BOX), 75L);

                        Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "We doen een extra formation lap.");
                        return true;

                    case "formationlap" :
                        runLights(world, grid_1x, y2, grid_1z, Material.RED_SHULKER_BOX);
                        runLights(world, grid_1x, y1, grid_1z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_2x, y2, grid_2z, Material.RED_SHULKER_BOX);
                        runLights(world, grid_2x, y1, grid_2z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_3x, y2, grid_3z, Material.RED_SHULKER_BOX);
                        runLights(world, grid_3x, y1, grid_3z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_4x, y2, grid_4z, Material.RED_SHULKER_BOX);
                        runLights(world, grid_4x, y1, grid_4z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_5x, y2, grid_5z, Material.RED_SHULKER_BOX);
                        runLights(world, grid_5x, y1, grid_5z, Material.BLACK_SHULKER_BOX);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_1x, y1, grid_1z, Material.BLACK_SHULKER_BOX), 1200L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_1x, y2, grid_1z, Material.BLACK_SHULKER_BOX), 1200L);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y1, grid_2z, Material.BLACK_SHULKER_BOX), 2400L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y2, grid_2z, Material.BLACK_SHULKER_BOX), 2400L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "3 min. tot de formatieronde, begin met de grote karren van de baan te krijgen"), 2400L);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y1, grid_3z, Material.BLACK_SHULKER_BOX), 3600L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y2, grid_3z, Material.BLACK_SHULKER_BOX), 3600L);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y1, grid_4z, Material.BLACK_SHULKER_BOX), 4800L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y2, grid_4z, Material.BLACK_SHULKER_BOX), 4800L);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "1 min. tot de formatieronde, ga alvast in de auto zitten!"), 4800L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "30 sec. tot de formatieronde, ga naar de zijkant van de grid!"), 5400L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "10 sec. tot de formatieronde!"), 5800L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "5 sec. tot de formatieronde!"), 5900L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "4 sec. tot de formatieronde!"), 5920L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "3 sec. tot de formatieronde!"), 5940L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "2 sec. tot de formatieronde!"), 5960L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "1 sec. tot de formatieronde!"), 5980L);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + ChatColor.GOLD + "De formatieronde is begonnen!"), 6000L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y1, grid_5z, Material.BLACK_SHULKER_BOX), 6000L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y2, grid_5z, Material.BLACK_SHULKER_BOX), 6000L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_2x, y1, grid_2z, Material.GREEN_SHULKER_BOX), 6000L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_4x, y1, grid_4z, Material.GREEN_SHULKER_BOX), 6000L);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_1x, y1, grid_1z, Material.GREEN_SHULKER_BOX), 6200L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_3x, y1, grid_3z, Material.GREEN_SHULKER_BOX), 6200L);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> runLights(world, grid_5x, y1, grid_5z, Material.GREEN_SHULKER_BOX), 6200L);

                        return true;

                    case "off" :
                        runLights(world, grid_1x, y1, grid_1z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_1x, y2, grid_1z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_2x, y1, grid_2z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_2x, y2, grid_2z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_3x, y1, grid_3z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_3x, y2, grid_3z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_4x, y1, grid_4z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_4x, y2, grid_4z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_5x, y1, grid_5z, Material.BLACK_SHULKER_BOX);
                        runLights(world, grid_5x, y2, grid_5z, Material.BLACK_SHULKER_BOX);

                        return true;
                }
            }else {
                //no args message
            }
        }else {
            //no permission message
        }

        return false;
    }

    public void runLights(World world, int x, int y, int z, Material material) {
        Location location = new Location(world, x, y, z);
        location.getBlock().setType(material);
    }
}
