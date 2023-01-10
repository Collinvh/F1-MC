package collinvht.projectr.inventory;

import collinvht.projectr.util.objects.LimitedObject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class InventoryBase {

    static ItemStack createItem(String name, Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }

    static ItemStack createItem(String item, Material material, int data) {
        ItemStack stack = createItem(item, material);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(data);
        stack.setItemMeta(meta);
        return stack;
    }

    static ItemStack createInformativeStack(String name, Material material, LimitedObject<Float> obj) {
        ItemStack item = createItem(name, material);
        ArrayList<String> info = new ArrayList<>();
        info.add(ChatColor.RESET + "Current : " + obj.getValue());
        info.add(ChatColor.RESET + "Limits : " + obj.getBottomLimit() + "/" + obj.getTopLimit());
        ItemMeta meta = item.getItemMeta();
        meta.setLore(info);
        item.setItemMeta(meta);

        return item;
    }

    static ItemStack createInformativeStack(String name, Material material, LimitedObject<Float> obj, int data) {
        ItemStack item = createInformativeStack(name, material, obj);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(data);
        item.setItemMeta(meta);
        return item;
    }
}
