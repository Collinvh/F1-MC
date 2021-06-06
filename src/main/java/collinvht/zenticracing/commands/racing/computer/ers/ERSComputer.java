package collinvht.zenticracing.commands.racing.computer.ers;

import collinvht.zenticracing.commands.racing.computer.RaceCar;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.object.TeamObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ERSComputer {
    public static String title = ChatColor.GREEN + "RacePC";
    public static String preTitle = ChatColor.GRAY + "Kies je driver";
    public static String prefix = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;

    public static void openInventory(Player player) {
        TeamObject teamObject = Team.checkTeamForPlayer(player);

        if(teamObject != null) {
            ArrayList<RaceCar> raceCars = teamObject.getRaceCars();

            if(raceCars.size() > 0) {
                Inventory prepc = Bukkit.createInventory(null, 27, preTitle);

                ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta m = item.getItemMeta();
                m.setDisplayName(" ");
                item.setItemMeta(m);
                for(int i=0; i<27; i++) {
                    prepc.setItem(i, item.clone());
                }

                AtomicInteger count = new AtomicInteger();
                raceCars.forEach(car -> {
                    if(count.get() <= 6) {
                        if(car.getDriverObject() != null) {
                            ItemStack stack = new ItemStack(Material.CACTUS);
                            ItemMeta meta = stack.getItemMeta();
                            meta.setDisplayName(car.getDriverObject().getPlayer().getName());
                            stack.setItemMeta(meta);
                            prepc.setItem(10 + count.get(), stack);
                            count.getAndIncrement();
                        }
                    }
                });

                player.openInventory(prepc);
            } else {
                player.sendMessage(prefix + "Er rijd niemand voor je team!");
            }
        }
    }

    public static void openRace(Player player, RaceCar car) {
        Inventory racePC = Bukkit.createInventory(null, 45, title);

        for(int size = 0; size<45; size++) {
            if(size == 0) {
                racePC.setItem(size, createItem(player.getName(), Material.GRAY_STAINED_GLASS_PANE));
            } else if(size == 11) {
                racePC.setItem(size, createItem(ChatColor.WHITE + "ERS OFF", Material.WHITE_STAINED_GLASS_PANE));
            } else if(size == 13) {
                racePC.setItem(size, createItem(ChatColor.YELLOW + "ERS REGULAR", Material.YELLOW_STAINED_GLASS_PANE));
            } else if(size == 15) {
                racePC.setItem(size, createItem(ChatColor.RED + "ERS PUSH", Material.RED_STAINED_GLASS_PANE));
            } else if(size == 29) {
                racePC.setItem(size, createItem(ChatColor.WHITE + "FM LOW", Material.WHITE_STAINED_GLASS_PANE));
            } else if(size == 31) {
                racePC.setItem(size, createItem(ChatColor.YELLOW + "FM REGULAR", Material.YELLOW_STAINED_GLASS_PANE));
            } else if(size == 33) {
                racePC.setItem(size, createItem(ChatColor.RED + "FM PUSH", Material.RED_STAINED_GLASS_PANE));
            } else {
                racePC.setItem(size, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(racePC);
    }

    public static ItemStack createItem(String name, Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }
}
