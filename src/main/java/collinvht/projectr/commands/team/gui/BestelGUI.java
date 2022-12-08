package collinvht.projectr.commands.team.gui;

import collinvht.projectr.commands.team.object.TeamObject;
import collinvht.projectr.util.ConfigUtil;
import collinvht.projectr.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BestelGUI {
    public static String title = ChatColor.GREEN + "BestelGUI";
    public static String prefix = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;

    public static void openInventory(Player player, TeamObject teamObject) {
        if(teamObject != null) {
            if(!ConfigUtil.canBestell()) {
                player.sendMessage(prefix + "Je kunt op dit moment niet bestellen.");
                return;
            }

            Inventory prepc = Bukkit.createInventory(null, 27, title);

            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName(" ");
            item.setItemMeta(m);
            for(int i=0; i<27; i++) {
                prepc.setItem(i, item.clone());
            }

            prepc.setItem(1, createItem("Add 1", Material.LIME_STAINED_GLASS_PANE, "Add 1 band bij de soft."));
            prepc.setItem(2, createItem("Add 1", Material.LIME_STAINED_GLASS_PANE, "Add 1 band bij de medium."));
            prepc.setItem(3, createItem("Add 1", Material.LIME_STAINED_GLASS_PANE, "Add 1 band bij de hard."));
            prepc.setItem(4, createItem("Add 1", Material.LIME_STAINED_GLASS_PANE, "Add 1 band bij de inter."));
            prepc.setItem(5, createItem("Add 1", Material.LIME_STAINED_GLASS_PANE, "Add 1 band bij de wet."));
            prepc.setItem(7, createItem("Add 1", Material.LIME_STAINED_GLASS_PANE, "Add 1 stack bij de fuel."));

            prepc.setItem(9, createItem("Cancel", Material.ORANGE_STAINED_GLASS));

            prepc.setItem(10, createItem("Soft Band", Material.DIAMOND_ORE));
            prepc.setItem(11, createItem("Medium Band", Material.DIAMOND_ORE));
            prepc.setItem(12, createItem("Hard Band", Material.DIAMOND_ORE));
            prepc.setItem(13, createItem("Intermediate Band", Material.DIAMOND_ORE));
            prepc.setItem(14, createItem("Wet Band", Material.DIAMOND_ORE));
            prepc.setItem(16, createItem("Fuel", Material.DIAMOND_ORE));

            prepc.setItem(17, createItem("Confirm", Material.ORANGE_STAINED_GLASS));

            prepc.setItem(19, createItem("Remove 1", Material.RED_STAINED_GLASS, "Remove 1 band bij de soft."));
            prepc.setItem(20, createItem("Remove 1", Material.RED_STAINED_GLASS, "Remove 1 band bij de medium."));
            prepc.setItem(21, createItem("Remove 1", Material.RED_STAINED_GLASS, "Remove 1 band bij de hard."));
            prepc.setItem(22, createItem("Remove 1", Material.RED_STAINED_GLASS, "Remove 1 band bij de inter."));
            prepc.setItem(23, createItem("Remove 1", Material.RED_STAINED_GLASS, "Remove 1 band bij de wet."));
            prepc.setItem(25, createItem("Remove 1", Material.RED_STAINED_GLASS, "Remove 1 band bij de fuel."));


            player.openInventory(prepc);

            DebugUtil.debugMessage("Bestelling PC geopend voor : " + player.getName());

        } else {
            player.sendMessage(prefix + "Jij zit niet in een team?");
        }
    }

    public static ItemStack createItem(String name, Material material, String... lore) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        if(Arrays.asList(lore).size() > 0) {
            List<String> list = new ArrayList<>(Arrays.asList(lore));
            meta.setLore(list);
        }
        pane.setItemMeta(meta);
        return pane;
    }
}
