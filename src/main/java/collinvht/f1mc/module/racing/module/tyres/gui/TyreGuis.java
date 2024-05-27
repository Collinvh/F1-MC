package collinvht.f1mc.module.racing.module.tyres.gui;

import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class TyreGuis {
    public static Gui allTyres = Gui.normal().setStructure(
            "# # # # # # # # #",
            "# # S # M # H # #",
            "# # # # # # # # #").addIngredient('S', new SimpleItem(TyreManager.getTyre("soft"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("soft")))).addIngredient('M', new SimpleItem(TyreManager.getTyre("medium"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("medium")))).addIngredient('H', new SimpleItem(TyreManager.getTyre("hard"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("hard")))).build();
    public static void reload() {
        for (Player allCurrentViewer : allTyres.findAllCurrentViewers()) {
            allCurrentViewer.closeInventory();
        }
        allTyres = Gui.normal().setStructure(
                "# # # # # # # # #",
                "# # S # M # H # #",
                "# # # # # # # # #").addIngredient('S', new SimpleItem(TyreManager.getTyre("soft"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("soft")))).addIngredient('M', new SimpleItem(TyreManager.getTyre("medium"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("medium")))).addIngredient('H', new SimpleItem(TyreManager.getTyre("hard"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("hard")))).build();
    }
}
