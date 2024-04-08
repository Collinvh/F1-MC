package collinvht.f1mc.module.timetrial.command;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.timetrial.obj.TimeTrialSession;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.commands.CommandUtil;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.api.objects.spawn.SpawnMode;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.Seat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class TimeTrialManager extends CommandUtil {
    private final Gui gui;
    @Getter
    private static final HashMap<UUID, TimeTrialSession> sessionHashMap = new HashMap<>();
    public TimeTrialManager() {
        ItemStack stack = Utils.createSkull(43876, "&8Malaysia GP");
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aMalaysia GP"));
            ArrayList<String> strings = new ArrayList<>();
            strings.add(ChatColor.DARK_GRAY + "Click to start timetrial!");
            meta.setLore(strings);
            stack.setItemMeta(meta);
        }
        stack.setItemMeta(meta);
        gui = Gui.normal().setStructure(
                "# # # # # # # # #",
                "# # # # A # # # #",
                "# # # # # # # # #").addIngredient('A', new SimpleItem(stack, click -> {
            Race race = RaceManager.getInstance().getRace("sepang");
            if(race != null) {
                Optional<BaseVehicle> baseVehicle = VehiclesPlusAPI.getInstance().getBaseVehicleFromString("F1Car");
                Bukkit.getLogger().warning("test");
                if(baseVehicle.isPresent()) {
                    Bukkit.getLogger().warning("isPresent");
                    SpawnedVehicle vehicle = VehiclesPlusAPI.getInstance().createVehicle(baseVehicle.get(), click.getPlayer()).spawnVehicle(race.getStorage().getTimeTrialSpawn(), SpawnMode.FORCE);
                    vehicle.getPartList().forEach(part -> {
                        if(part instanceof Seat) {
                            Seat seat = (Seat) part;
                            if(seat.getSteer()) {
                                sessionHashMap.put(click.getPlayer().getUniqueId(), new TimeTrialSession(click.getPlayer(), click.getPlayer().getLocation(), vehicle, race));
                                click.getPlayer().teleport(race.getStorage().getTimeTrialSpawn());
                                seat.enter(click.getPlayer());
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if (!onlinePlayer.getUniqueId().equals(click.getPlayer().getUniqueId())) {
                                        onlinePlayer.hidePlayer(F1MC.getInstance(), click.getPlayer());
                                        onlinePlayer.hideEntity(F1MC.getInstance(), vehicle.getHolder());
                                    }
                                }
                            }
                        }
                    });
                }
            }
        })).addIngredient('#', new SimpleItem(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))).build();
    }

    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/timetrial", (sender, command, s, strings) -> {
            if(!(sender instanceof Player)) return prefix + "Need to be player";
            Player player = (Player) sender;
            if(sessionHashMap.get(player.getUniqueId()) != null) return prefix + "You are in a session already.";
            Window.single().setGui(gui).build(player).open();
            return prefix + "Timetrial menu opened.";
        });
    }
}
