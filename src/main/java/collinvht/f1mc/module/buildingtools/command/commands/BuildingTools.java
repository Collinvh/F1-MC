package collinvht.f1mc.module.buildingtools.command.commands;

import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class BuildingTools extends CommandUtil {
    @Getter
    private static final ArrayList<UUID> players = new ArrayList<>();
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("toggle", 0, "/buildingtools toggle", ((sender, command, label, args) -> {
            if(sender instanceof Player) {
                UUID uuid = ((Player) sender).getUniqueId();
                if(!players.contains(uuid)) {
                    players.add(uuid);
                    return prefix + "Building tools enabled";
                } else {
                    players.remove(uuid);
                    return prefix + "Building tools disabled";
                }
            } else {
                return prefix + "Only a player can do this.";
            }
        }), Permissions.FIA_ADMIN, Permissions.BUILDER);
    }
}