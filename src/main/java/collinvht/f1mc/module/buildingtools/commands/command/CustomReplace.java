package collinvht.f1mc.module.buildingtools.commands.command;

import collinvht.f1mc.module.buildingtools.manager.CustomManager;
import collinvht.f1mc.module.buildingtools.obj.CombinedBlocks;
import collinvht.f1mc.module.buildingtools.obj.MemorizedEdit;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.commands.CommandUtil;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomReplace extends CommandUtil implements TabCompleter {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 1, "/creplace [block] [cblock]", (sender, command, label, args) -> {
            if(!(sender instanceof Player)) return prefix + "Only a player can do that";
            try {
                Region region = Utils.getSession((Player) sender).getSelection(Utils.getAdaptedWorld(((Player) sender).getWorld()));
                World world = ((Player) sender).getWorld();
                Material material = Material.getMaterial(args[0].toUpperCase());
                if(material == null) return prefix + "Block doesn't exist";
                CustomBlock cblock = CustomBlock.getInstance(args[1]);
                if(cblock == null) return prefix + "Custom block doesn't exist";
                MemorizedEdit edit = new MemorizedEdit();
                region.forEach(blockVector3 -> {
                    Block block = new Location(world, blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ()).getBlock();
                    Material bmat = block.getType();
                    if(bmat == material) {
                        Location location = block.getLocation();
                        CustomBlock nextBlock = CustomBlock.getInstance(args[1]);
                        edit.addEdit(new CombinedBlocks(nextBlock, location, bmat));
                    }
                });
                CustomManager.addEdit(((Player) sender).getUniqueId(), edit);
                edit.runEdit();
            } catch (IncompleteRegionException e) {
                return prefix + "You dont have a valid selection.";
            }
            return prefix + "Blocks replaced";
        }, Permissions.BUILDER);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(Permissions.BUILDER.hasPermission(commandSender)) {
            if(args.length == 1) {
                ArrayList<String> list = new ArrayList<>();
                for (Material value : Material.values()) {
                    if(value.isSolid()) list.add(value.name().toLowerCase());
                }
                return list;
            }
            if(args.length == 2) {
                ArrayList<String> list = new ArrayList<>();
                for (Object allItem : ItemsAdder.getAllItems()) {
                    if(allItem instanceof CustomStack stack) {
                        if(stack.isBlock()) list.add(stack.getNamespace() + ":" + stack.getId());
                    }
                }
                return list;
            }
        }
        return null;
    }
}
