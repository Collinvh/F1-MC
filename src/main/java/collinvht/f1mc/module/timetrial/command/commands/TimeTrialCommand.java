package collinvht.f1mc.module.timetrial.command.commands;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.timetrial.manager.TimeTrialManager;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.commands.CommandUtil;
import com.mysql.cj.jdbc.MysqlDataSource;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.Seat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TimeTrialCommand extends CommandUtil implements TabCompleter {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/timetrial", ((sender, command, label, args) -> {
            if(sender instanceof Player player) {
                return prefix + TimeTrialManager.openGUI(player);
            } else {
                return prefix + "Only a player can do this.";
            }
        }));
        addPart("reset", 1, "/timetrial reset [circuit]", (((sender, command, label, args) -> {
            if(sender instanceof Player player) {
                return prefix + TimeTrialManager.resetLap(player.getUniqueId(), args[1]);
            } else {
                return prefix +"Your nota a player";
            }
        })));
        addPart("rival", 2, "/timetrial rival [track] [name]", ((sender, command, label, args) -> {
            if(sender instanceof Player player) {
                return prefix + TimeTrialManager.setRival(player.getUniqueId(), args[1], args[2]);
            } else return prefix + "You have to be a player";
        }));
        addPart("car", 1, "/timetrial car [name]", ((sender, command, label, args) -> {
            if(sender instanceof Player player) {
                if (args[1].equals("f1car")) {
                    TimeTrialManager.removeF1CarPrefrence(player.getUniqueId());
                } else {
                    Optional<BaseVehicle> vehicle = VehiclesPlusAPI.getInstance().getBaseVehicleFromString(args[1].toLowerCase());
                    if(vehicle.isPresent()) {
                        TimeTrialManager.addF1CarPreference(((Player) sender).getUniqueId(), args[1].toLowerCase());
                    } else {
                        return prefix + "Invalid car name.";
                    }
                }
            }
            return prefix + "Changed Preference";
        }));
        addPart("fastest", 1, "/timetrial fastest [name] {page}", ((sender, command, label, args) -> {
            MysqlDataSource dataSource = Utils.getDatabase();
            try {
                Connection connection = dataSource.getConnection();
                int offset = 0;
                if(args.length > 2) {
                    try {
                        int offsetValue = Integer.parseInt(args[2])-1;
                        if(offsetValue > 0) offset = 5 * offsetValue;
                    } catch (NumberFormatException e) {
                        return prefix + "Invalid number";
                    }
                }
                Race race = RaceManager.getInstance().getRace(args[1]);
                if(race == null) return prefix + "That race doesnt exist";

                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM timetrial_laps WHERE `track_name`= '"+ race.getName() +"' ORDER BY `lap_length` ASC LIMIT 5 "+ (offset > 0 ? "OFFSET " + offset : "") +";");
                ResultSet rs = stmt.executeQuery();
                StringBuilder list = new StringBuilder();
                int number = offset;
                while (rs.next()) {
                    number += 1;
                    UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    list.append(number).append(". ").append(player.getName()).append(" : ").append(Utils.millisToTimeString(rs.getLong("lap_length"))).append(" Sectors: \nS1: ").append(Utils.millisToTimeString(rs.getLong("s1_length"))).append(" | S2: ").append(Utils.millisToTimeString(rs.getLong("s2_length"))).append(" | S3: ").append(Utils.millisToTimeString(rs.getLong("s3_length"))).append("\n");
                }
                if(!list.isEmpty()) {
                    return prefix + "Top 5"+ (offset != 0 ? " on Page " + (offset/5)+1 : "") +":\n" + list + "=-=-=-=-=-=-=-=-=-=-=-=-=";
                } else {
                    return prefix + "No drivers in this tab";
                }
            } catch (SQLException e) {
                Bukkit.getLogger().warning(e.getMessage());
            }
            return "";
        }));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(commandSender instanceof Player) {
            ArrayList<String> list = new ArrayList<>();
            if(args.length == 2) {
                return switch (args[0].toLowerCase()) {
                    default -> null;
                    case "reset", "rival", "fastest" -> {
                        RaceManager.getRACES().forEach((s1, race) -> {
                            list.add(race.getName());
                        });
                        yield list;
                    }
                    case "car" -> {
                        VehiclesPlusAPI.getVehicleManager().getBaseVehicleMap().forEach((s1, baseVehicle) -> list.add(s1));
                        yield list;
                    }
                };
            } else if (args.length == 1){
                list.add("fastest");
                list.add("fastest");
                list.add("car");
                list.add("reset");
                list.add("rival");
            }
            return list;
        }
        return null;
    }
}
