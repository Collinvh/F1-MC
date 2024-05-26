package collinvht.f1mc.module.buildingtools.commands.command;

import collinvht.f1mc.module.buildingtools.manager.CustomManager;
import collinvht.f1mc.module.buildingtools.obj.CombinedBlocks;
import collinvht.f1mc.module.buildingtools.obj.MemorizedEdit;
import collinvht.f1mc.util.commands.CommandUtil;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomReplacenear extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 2, "/creplacenear [range] [block] [cblock]", (sender, command, label, args) -> {
            if(!(sender instanceof Player)) return prefix + "Only a player can do that";
            try {
                int value = Integer.parseInt(args[0]);
                if(!(value >0)) return prefix + "Positive number required";
                if(value > 200) return prefix + "200 is the max.";
                List<Block> blocks = getNearbyBlocks(((Player) sender).getLocation(), value);
                Material material = Material.getMaterial(args[1].toUpperCase());
                if(material == null) return prefix + "Block doesn't exist";
                CustomBlock cblock = CustomBlock.getInstance(args[2]);
                if(cblock == null) return prefix + "Custom block doesn't exist";
                MemorizedEdit edit = new MemorizedEdit();
                for (Block block : blocks) {
                    Material bmat = block.getType();
                    if(bmat == material) {
                        Location location = block.getLocation();
                        CustomBlock nextBlock = CustomBlock.getInstance(args[3]);
                        edit.addEdit(new CombinedBlocks(nextBlock, location, bmat));
                    }
                }
                CustomManager.addEdit(((Player) sender).getUniqueId(), edit);
                edit.runEdit();
            } catch (NumberFormatException e) {
                return prefix + "Number is invalid.";
            }
            return prefix + "Blocks replaced";
        });
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
}
