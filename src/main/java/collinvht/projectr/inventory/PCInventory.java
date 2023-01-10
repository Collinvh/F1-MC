package collinvht.projectr.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PCInventory extends InventoryBase {
    public static String title = ChatColor.GRAY + "Computer.";

    public static void openFirst(HumanEntity player) {
        Inventory prePc = Bukkit.createInventory(null, 27, title);

        for(int i = 0; i<27; i++) {
            if(i == 11) {
                prePc.setItem(i, createItem("ERS", Material.PAPER, 10008));
            } else if(i == 15) {
                prePc.setItem(i, createItem("Setup", Material.FEATHER));
            } else {
                prePc.setItem(i, createItem(" ", Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(prePc);
    }

    public static void runEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(title)) {
            if (event.getCurrentItem() != null) {
                HumanEntity player = event.getView().getPlayer();
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("ERS")) {
                    //openAero(player);
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Setup")) {
                    SetupInventory.openFirst(player);
                }
            }

            event.setCancelled(true);
        }
    }
}
