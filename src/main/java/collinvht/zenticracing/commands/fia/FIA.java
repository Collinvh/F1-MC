package collinvht.zenticracing.commands.fia;

import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.commands.racing.computer.band.BandGUI;
import collinvht.zenticracing.commands.racing.computer.ers.ERSComputer;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.object.TeamObject;
import collinvht.zenticracing.listener.driver.DriverManager;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import collinvht.zenticracing.util.DebugUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class FIA implements CommandUtil {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("zentic.fia")) {
                Player player = (Player) sender;
                if(args.length > 0) {
                    switch (args[0]) {
                        case "debug":
                            if (sender.hasPermission("zentic.dev")) {
                                if (DebugUtil.containsPlayer(player)) {
                                    DebugUtil.removeDebuggingPlayer(player);
                                    player.sendMessage(prefix + "Debug mode uitgezet");
                                } else {
                                    DebugUtil.addDebuggingPlayer(player);
                                    player.sendMessage(prefix + "Debug mode aangezet");
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
                                                sender.sendMessage(prefix + "Je zit niet in een auto?");
                                                return false;
                                            }
                                            if (object1.getCurvehicle() == null) {
                                                sender.sendMessage(prefix + "Je zit niet in een auto?");
                                                return false;
                                            }

                                            RaceCar raceCar2 = new RaceCar(object1.getCurvehicle(), object);
                                            raceCar2.setDriverObject(object1);
                                            object.addRaceCar(raceCar2);
                                            sender.sendMessage(prefix + "Car added");
                                            break;
                                        case "remove":
                                            if (object1 == null) {
                                                sender.sendMessage(prefix + "Je zit niet in een auto?");
                                                return false;
                                            }
                                            if (object1.getCurvehicle() == null) {
                                                sender.sendMessage(prefix + "Je zit niet in een auto?");
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
                                                sender.sendMessage(prefix + "Car removed");
                                            } else {
                                                sender.sendMessage(prefix + "Je zit niet in een auto?");
                                            }
                                            break;
                                        case "openband":
                                            BandGUI.open(player, object);
                                            sender.sendMessage(prefix + "GUI Geopend");
                                            break;
                                        case "openers":
                                            ERSComputer.openInventory(player, object);
                                            sender.sendMessage(prefix + "GUI Geopend");
                                            break;
                                    }
                                } else{
                                    sender.sendMessage(prefix + "Team bestaat niet.");
                                }
                            } else {
                                sender.sendMessage(prefix + "Usage /fia racecar [teamnaam] add/remove/openband");
                            }
                    }

                }
            }
        }
        return true;
    }
}
