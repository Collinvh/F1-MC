package collinvht.f1mc.module.buildingtools.commands.command;

import collinvht.f1mc.module.buildingtools.manager.CustomManager;
import collinvht.f1mc.module.buildingtools.obj.MemorizedEdit;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.commands.CommandUtil;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomUndo extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/cundo", (sender, command, label, args) -> {
            if(sender instanceof Player) return prefix + CustomManager.undo(((Player) sender).getUniqueId());
            return prefix + "You have to be a player for that.";
        });
    }
}
