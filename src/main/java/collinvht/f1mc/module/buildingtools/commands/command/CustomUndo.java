package collinvht.f1mc.module.buildingtools.commands.command;

import collinvht.f1mc.module.buildingtools.manager.CustomManager;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomUndo extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/cundo", (sender, command, label, args) -> {
            if(sender instanceof Player) return prefix + CustomManager.undo(((Player) sender).getUniqueId());
            return prefix + "You have to be a player for that.";
        }, Permissions.BUILDER);
    }
}
