package collinvht.f1mc.module.buildingtools.commands.command;

import collinvht.f1mc.module.buildingtools.manager.CustomManager;
import collinvht.f1mc.module.buildingtools.obj.CombinedBlocks;
import collinvht.f1mc.module.buildingtools.obj.MemorizedEdit;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.commands.CommandUtil;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomSet extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/cset [cblock]", (sender, command, label, args) -> {
            if(!(sender instanceof Player)) return prefix + "Only a player can do that";
            try {
                Region region = Utils.getSession((Player) sender).getSelection(Utils.getAdaptedWorld(((Player) sender).getWorld()));
                World world = ((Player) sender).getWorld();
                CustomBlock cblock = CustomBlock.getInstance(args[1]);
                if(cblock == null) return prefix + "Custom block doesn't exist";

                MemorizedEdit edit = new MemorizedEdit();
                region.forEach(blockVector3 -> {
                    Block block = new Location(world, blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ()).getBlock();
                    Material bmat = block.getType();
                    Location location = block.getLocation();
                    CustomBlock nextBlock = CustomBlock.getInstance(args[1]);
                    edit.addEdit(new CombinedBlocks(nextBlock, location, bmat));
                });
                CustomManager.addEdit(((Player) sender).getUniqueId(), edit);
                edit.runEdit();
            } catch (IncompleteRegionException e) {
                return prefix + "You dont have a valid selection.";
            }
            return prefix + "Blocks set";
        });
    }
}
