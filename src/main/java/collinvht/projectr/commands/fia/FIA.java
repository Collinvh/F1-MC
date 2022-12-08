package collinvht.projectr.commands.fia;

import collinvht.projectr.commands.CommandUtil;
import collinvht.projectr.commands.racing.computer.RaceCar;
import collinvht.projectr.commands.racing.computer.band.BandGUI;
import collinvht.projectr.commands.racing.computer.ers.ERSComputer;
import collinvht.projectr.commands.team.Team;
import collinvht.projectr.commands.team.object.TeamObject;
import collinvht.projectr.listener.driver.DriverManager;
import collinvht.projectr.listener.driver.object.DriverObject;
import collinvht.projectr.util.DebugUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class FIA implements CommandUtil {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("projectr.fia")) {
                Player player = (Player) sender;
                if(args.length > 0) {
                    switch (args[0]) {
                        case "debug":
                            if (sender.hasPermission("projectr.dev")) {
                                if (DebugUtil.containsPlayer(player)) {
                                    DebugUtil.removeDebuggingPlayer(player);
                                    player.sendMessage(serverPrefix + "Debug mode uitgezet");
                                } else {
                                    DebugUtil.addDebuggingPlayer(player);
                                    player.sendMessage(serverPrefix + "Debug mode aangezet");
                                }
                            }
                            break;
                        case "racecar":
                            if (args.length > 2) {
                                TeamObject object = Team.getTeamObj().get(args[1].toLowerCase());
                                if (object != null) {
                                    DriverObject object1 = DriverManager.getDriver(player.getUniqueId());
                                    switch (args[2]) {
                                        case "add":
                                            if (object1 == null) {
                                                sender.sendMessage(serverPrefix + "Je zit niet in een auto?");
                                                return false;
                                            }
                                            if (object1.getCurvehicle() == null) {
                                                sender.sendMessage(serverPrefix + "Je zit niet in een auto?");
                                                return false;
                                            }

                                            RaceCar raceCar2 = new RaceCar(object1.getCurvehicle(), object);
                                            raceCar2.setDriverObject(object1);
                                            object.addRaceCar(raceCar2);
                                            sender.sendMessage(serverPrefix + "Car added");
                                            break;
                                        case "remove":
                                            if (object1 == null) {
                                                sender.sendMessage(serverPrefix + "Je zit niet in een auto?");
                                                return false;
                                            }
                                            if (object1.getCurvehicle() == null) {
                                                sender.sendMessage(serverPrefix + "Je zit niet in een auto?");
                                                return false;
                                            }

                                            AtomicReference<RaceCar> rc = new AtomicReference<>();
                                            object.getRaceCars().forEach(raceCar -> {
                                                if (raceCar.getSpawnedVehicle() != null) {
                                                    if (raceCar.getSpawnedVehicle().getStorageVehicle().getUuid().equals(object1.getCurvehicle().getStorageVehicle().getUuid())) {
                                                        rc.set(raceCar);
                                                    }
                                                }
                                            });
                                            if (rc.get() != null) {
                                                object.getRaceCars().remove(rc.get());
                                                sender.sendMessage(serverPrefix + "Car removed");
                                            } else {
                                                sender.sendMessage(serverPrefix + "Je zit niet in een auto?");
                                            }
                                            break;
                                        case "openband":
                                            BandGUI.open(player, object);
                                            sender.sendMessage(serverPrefix + "GUI Geopend");
                                            break;
                                        case "openers":
                                            ERSComputer.openInventory(player, object);
                                            sender.sendMessage(serverPrefix + "GUI Geopend");
                                            break;
                                    }
                                } else{
                                    sender.sendMessage(serverPrefix + "Team bestaat niet.");
                                }
                            } else {
                                sender.sendMessage(serverPrefix + "Usage /fia racecar [teamnaam] add/remove/openband");
                            }
                    }

                }
            }
        }
        return true;
    }
}
