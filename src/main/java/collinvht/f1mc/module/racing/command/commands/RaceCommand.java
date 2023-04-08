package collinvht.f1mc.module.racing.command.commands;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RaceCommand extends CommandUtil {
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
        addPart("update", 3, "/race update [name] [type] [input]", (sender, command, label, args) -> racing.updateRace((Player) sender, args[1].toLowerCase(), args[2], args[3]), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
        //addPart("tyres", 1, "/race tyres get", (sender, command, label, args) -> TyreInventory.openList((Player) sender), Permissions.FIA_RACE, Permissions.FIA_ADMIN);
    }
}
