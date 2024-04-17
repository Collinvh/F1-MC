package collinvht.f1mc.module.racing.command.commands;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RaceCommand extends CommandUtil implements TabCompleter {
    private static final RaceManager racing = RaceManager.getInstance();

    /**
     * @author Collinvht
     * See Trello:
     * <a href="https://trello.com/c/oHkXjewh">...</a>
     */
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("start", 2, "/race start [name] [mode]", (sender, command, label, args) -> racing.startRace(args[1].toLowerCase(), args[2]), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        addPart("stop", 1, "/race stop [name]", (sender, command, label, args) -> racing.stopRace(args[1].toLowerCase()), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        addPart("reset", 1, "/race reset [name]", (sender, command, label, args) -> racing.resetRace(args[1].toLowerCase()), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        addPart("toimage", 1, "/race toimage [name]", (sender, command, label, args) -> racing.toImage(args[1].toLowerCase()), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        addPart("delete", 1, "/race delete [name]", (sender, command, label, args) -> racing.deleteRace(args[1]), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        addPart("get", 2, "/race get [name] [type]", (sender, command, label, args) -> racing.getRaceResult(args[1].toLowerCase(), args[2].toLowerCase(), ((Player) sender).getUniqueId()));
        addPart("create", 2, "/race create [name] [laps]", (sender, command, label, args) -> racing.createRace(args[1], args[2]), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        addPart("list", 0, "/race list", (sender, command, label, args) -> racing.listRaces(), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        addPart("update", 3, "/race update [name] [type] [input]", (sender, command, label, args) -> racing.updateRace((Player) sender, args[1].toLowerCase(), args[2], args[3], args), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        //addPart("tyres", 1, "/race tyres get", (sender, command, label, args) -> TyreInventory.openList((Player) sender), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] arg) {
        if(Permissions.FIA_RACE.hasPermission(commandSender) || Permissions.FIA_ADMIN.hasPermission(commandSender)) {
            ArrayList<String> list = new ArrayList<>();
            if (arg.length == 2) {
                return switch (arg[0]) {
                    case "stop", "reset", "toimage", "delete", "get", "update", "start" -> {
                        RaceManager.getRACES().forEach((s1, race) -> list.add(race.getName()));
                        yield list;
                    }
                    default -> list;
                };
            }
            if(arg.length == 3) {
                return switch (arg[0]) {
                    case "start" -> {
                        for (RaceMode value : RaceMode.values()) {
                            list.add(String.valueOf(value.getModeId()));
                        }
                        yield list;
                    }
                    case "update" -> {
                        list.add("laps");
                        list.add("timetrial");
                        list.add("offtrack");
                        list.add("sector");
                        list.add("pitlane");
                        list.add("name");
                        yield list;
                    }
                    default -> null;
                };
            }
            if(arg.length == 4) {
                if(arg[0].equalsIgnoreCase("update")) {
                    return switch (arg[2]) {
                        case "timetrial" -> {
                            list.add("disable");
                            list.add("enable");
                            list.add("setspawn");
                            list.add("leaderboard");
                            yield list;
                        }
                        case "offtrack" -> {
                            list.add("delete");
                            list.add("list");
                            yield list;
                        }
                        case "sector" -> {
                            list.add("sector1");
                            list.add("sector2");
                            list.add("sector3");
                            list.add("finish");
                            list.add("mini_1");
                            list.add("mini_2");
                            list.add("mini_3");
                            yield list;
                        }
                        case "pitlane" -> {
                            list.add("pitentry");
                            list.add("pitexit");
                            yield list;
                        }
                        default -> null;
                    };
                }
            }
            if(arg.length == 6) {
                if(arg[0].equalsIgnoreCase("update")) {
                    Race race = RaceManager.getInstance().getRace(arg[1]);
                    if(race == null) return null;
                    if(arg[2].equalsIgnoreCase("offtrack")) {
                        if(arg[3].equalsIgnoreCase("delete")) {
                            race.getStorage().getLimits().forEach((s1, namedCuboid) -> list.add(s1));
                            return list;
                        }
                    }
                    if(arg[2].equalsIgnoreCase("sector")) {
                        if(arg[3].equalsIgnoreCase("mini_1")) {
                            if(arg[4].equalsIgnoreCase("delete")) {
                                race.getStorage().getS1_mini().forEach((s1, namedCuboid) -> list.add(s1));
                                return list;
                            }
                        }
                        if(arg[3].equalsIgnoreCase("mini_2")) {
                            if(arg[4].equalsIgnoreCase("delete")) {
                                race.getStorage().getS2_mini().forEach((s1, namedCuboid) -> list.add(s1));
                                return list;
                            }
                        }
                        if(arg[3].equalsIgnoreCase("mini_3")) {
                            if(arg[4].equalsIgnoreCase("delete")) {
                                race.getStorage().getS3_mini().forEach((s1, namedCuboid) -> list.add(s1));
                                return list;
                            }
                        }
                    }
                }
            }
            if(arg.length == 1) {
                list.add("start");
                list.add("stop");
                list.add("reset");
                list.add("toimage");
                list.add("delete");
                list.add("get");
                list.add("create");
                list.add("list");
                list.add("update");
                return list;
            }
        }

        return null;
    }
}
