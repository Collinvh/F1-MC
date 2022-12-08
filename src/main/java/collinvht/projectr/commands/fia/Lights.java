//package collinvht.projectr.commands.fia;
//
//import collinvht.projectr.ProjectR;
//import org.bukkit.*;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.Locale;
//import java.util.Random;
//
//public class Lights implements CommandExecutor {
//
//    private final ProjectR plugin = ProjectR.getRacing();
//
//    private final String currentCircuit = plugin.getConfig().getString("circuit");
//    private int time;
//
//    @Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//
//        String world1 = plugin.getConfig().getString(args[0] + ".world");
//        assert world1 != null;
//        World world = Bukkit.getWorld(world1);
//        Material red = Material.RED_SHULKER_BOX;
//        Material green = Material.GREEN_SHULKER_BOX;
//        Material yellow = Material.YELLOW_SHULKER_BOX;
//        Material black = Material.BLACK_SHULKER_BOX;
//        Material orange = Material.ORANGE_SHULKER_BOX;
//
//        Random random = new Random();
//
//        if (sender instanceof Player && sender.hasPermission("projectr.fia")) {
//            if (args.length > 2) {
//                switch (args[1].toLowerCase(Locale.ROOT)) {
//                    case "grid" :
//                        switch (args[2].toLowerCase(Locale.ROOT)) {
//                            case "resetcfg" :
//                                plugin.saveDefaultConfig();
//                            case "start" :
//                                for (Player p : Bukkit.getOnlinePlayers()) {
//                                    p.sendMessage("Pas gaan als alle lichten UIT zijn!");
//                                }
//
//                                runLights(plugin.getConfig().getInt(args[0] + ".grid.1.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.1.z"), world, red);
//                                runLights(plugin.getConfig().getInt(args[0] + ".grid.1.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.1.z"), world, red);
//
//                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.2.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.2.z"), world, red);
//                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.2.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.2.z"), world, red);
//
//                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                                        runLights(plugin.getConfig().getInt(args[0] + ".grid.3.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.3.z"), world, red);
//                                        runLights(plugin.getConfig().getInt(args[0] + ".grid.3.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.3.z"), world, red);
//
//                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                                            runLights(plugin.getConfig().getInt(args[0] + ".grid.4.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.4.z"), world, red);
//                                            runLights(plugin.getConfig().getInt(args[0] + ".grid.4.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.4.z"), world, red);
//
//                                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                                                runLights(plugin.getConfig().getInt(args[0] + ".grid.5.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.5.z"), world, red);
//                                                runLights(plugin.getConfig().getInt(args[0] + ".grid.5.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.5.z"), world, red);
//
//                                                int max = 60;
//                                                int min = 10;
//                                                int range = max - min + 1;
//
//                                                // generate random numbers within 1 to 10
//                                                for (int i = 0; i < 10; i++) {
//                                                    int rand = (int) (Math.random() * range) + min;
//
//                                                    // Output is different everytime this code is executed
//                                                    this.time = rand;
//                                                }
//
//                                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.1.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.1.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.1.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.1.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.2.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.2.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.2.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.2.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.3.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.3.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.3.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.3.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.4.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.4.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.4.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.4.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.5.x"), plugin.getConfig().getInt(args[0] + ".grid.y1"), plugin.getConfig().getInt(args[0] + ".grid.5.z"), world, black);
//                                                    runLights(plugin.getConfig().getInt(args[0] + ".grid.5.x"), plugin.getConfig().getInt(args[0] + ".grid.y2"), plugin.getConfig().getInt(args[0] + ".grid.5.z"), world, black);
//
//                                                    for (Player p : Bukkit.getOnlinePlayers()) {
//                                                        p.sendMessage("Race gestart!");
//                                                    }
//                                                }, time);
//                                            }, 15L);
//                                        }, 15L);
//                                    }, 15L);
//                                }, 15L);
//
//
//
//                                return true;
//                        }
//                        return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    private void runLights(int x, int y, int z, World world, Material material) {
//        Location l = new Location(world, x, y, z);
//        l.getBlock().setType(material);
//    }
//}