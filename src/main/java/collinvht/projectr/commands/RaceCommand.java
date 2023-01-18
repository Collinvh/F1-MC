package collinvht.projectr.commands;

import collinvht.projectr.util.objects.commands.CommandUtil;
import collinvht.projectr.manager.race.RacingManager;
import collinvht.projectr.util.enums.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RaceCommand extends CommandUtil {
    private static final RacingManager racing = RacingManager.getInstance();

    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        /*
        This starts the race.

        Mode is currently:
        Mode 1: Practice/Qualification
        Mode 2: Race
         */
        addPart("start", 2, "/race start [name] [mode]", (sender, command, label, args) -> racing.startRace(args[1].toLowerCase(), args[2]), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        /*
        This stops the race
         */
        addPart("stop", 0, "/race stop", (sender, command, label, args) -> racing.stopRace(), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        /*
        Resets the race to nothing
         */
        addPart("reset", 0, "/race reset", (sender, command, label, args) -> racing.resetRace(), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        /*
        Converts the race standings to a PNG file to upload
         */
        addPart("toimage", 0, "/race toimage", (sender, command, label, args) -> racing.toImage(), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        /*
        Deletes the race
         */
        addPart("delete", 1, "/race delete [name]", (sender, command, label, args) -> racing.deleteRace(args[1]), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        /*
        /race get [type]
        When a race is occurring you can use one of the following types to get information about it:

        Fastest
        Result
        Stand
        Record
         */
        addPart("get", 1, "/race get [type]", (sender, command, label, args) -> {
            if (sender instanceof Player) {
                return racing.getRaceResult(args[1].toLowerCase(), ((Player) sender).getUniqueId());
            } else return "You can only do this as a player.";
        });
        /*
        Creates a race with parameters
         */
        addPart("create", 2, "/race create [name] [laps]", (sender, command, label, args) -> racing.createRace(args[1], args[2]), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        /*
        Lists currently created races
         */
        addPart("list", 0, "/race list", (sender, command, label, args) -> racing.listRaces(), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        /*
        You can change one of the following types with this command:
        Laps
        Sector
        Pitlane
        Name
         */
        addPart("set", 3, "/race set [name] [type] [input]", (sender, command, label, args) -> racing.updateRace((Player) sender, args[1].toLowerCase(), args[2], args[3]), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
    }
}
