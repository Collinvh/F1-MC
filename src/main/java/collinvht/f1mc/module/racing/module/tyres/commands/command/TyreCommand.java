package collinvht.f1mc.module.racing.module.tyres.commands.command;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.tyres.gui.TyreGuis;
import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.module.racing.module.tyres.obj.TyreBaseObject;
import collinvht.f1mc.util.Permissions;
import collinvht.f1mc.util.commands.CommandUtil;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.window.Window;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TyreCommand extends CommandUtil implements TabCompleter {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("get", 0, "/tyre get", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                Window.single().setGui(TyreGuis.allTyres).build((Player) sender).open();
                return prefix + "Gui Opened";
            } else {
                return prefix + "Only a player can do this";
            }
        }, Permissions.FIA_ADMIN);
        addPart("debug", 0, "/tyre debug [Hold tyre in hand]", (sender, command, label, args) -> {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(TyreManager.isTyre(stack)) {
                    NBTItem item = new NBTItem(stack);
                    return prefix + "Tyre info:\nTyre was made for: " +
                            item.getString("f1mc.track") + "\nTyre dura: " +
                            item.getDouble("f1mc.dura") + "/" +
                            item.getDouble("f1mc.maxdura") + "\nTyre steering: " +
                            item.getDouble("f1mc.steering") + "\nTyre ExtraSpeed: " +
                            item.getDouble("f1mc.extraSpeed") + "\nTyre degrading rate: " +
                            item.getDouble("f1mc.degradationRate");
                }
                return prefix + "That item is not a tyre!";
            } else {
                return prefix + "You have to be a player for this.";
            }
        }, Permissions.FIA_COMMON);
        addPart("track",1, "/tyre track [trackname]", (sender, command, label, args) -> {
            if(RaceManager.getInstance().getRace(args[1].toLowerCase()) == null) sender.sendMessage(prefix + "Warning:\nThat track doesn't exist,\nthis isn't an issue but you might've forgotten to set it up!");
            TyreManager.setTrack(args[1].toLowerCase());
            return prefix + "Track changed";
        }, Permissions.FIA_ADMIN);
        addPart("set", 3, "/tyre set [tyre] [type] [value]", (sender, command, label, args) -> {
            String tyre = args[1].toLowerCase();
            TyreBaseObject object = TyreManager.getTyres().get(tyre);
            if(object == null) return prefix + "Tyre doesn't exist\nTyres are soft|medium|hard";
            String type = args[2].toLowerCase();
            switch (type) {
                case "dura":
                case "durability":
                    try {
                        object.setMaxDurability(Double.parseDouble(args[3]));
                    } catch (Exception ignored) {
                        return prefix + "Invalid number";
                    }
                case "deg":
                case "degradation":
                    try {
                        object.setDegradingRate(Double.parseDouble(args[3]));
                    } catch (Exception ignored) {
                        return prefix + "Invalid number";
                    }
                case "steer":
                case "steering":
                    try {
                        object.setSteering(Double.parseDouble(args[3]));
                    } catch (Exception ignored) {
                        return prefix + "Invalid number";
                    }
                case "speed":
                case "extraspeed":
                    try {
                        object.setExtraSpeed(Double.parseDouble(args[3]));
                    } catch (Exception ignored) {
                        return prefix + "Invalid number";
                    }
                default:
                    return prefix + "Invalid type\nTypes are durability|degradation|steering|extraspeed";
            }
        }, Permissions.FIA_ADMIN);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(Permissions.FIA_ADMIN.hasPermission(commandSender)) {
            ArrayList<String> list = new ArrayList<>();
            if(args.length == 1) {
                list.add("get");
                list.add("debug");
                list.add("track");
                list.add("set");
                return list;
            }
            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("track")) {
                    RaceManager.getRACES().forEach((s1, race) -> list.add(s1));
                }
                if(args[0].equalsIgnoreCase("set")) {
                    list.add("durability");
                    list.add("degradation");
                    list.add("steering");
                    list.add("extraspeed");
                }
                return list;
            }
        }
        return null;
    }
}
