package collinvht.f1mc.module.racing.module.tyres.gui;

import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class TyreGuis {
    public static Gui allTyres = Gui.normal().setStructure(
            "# # # # # # # # #",
            "# I # S M H # W #",
            "# # # # # # # # #").addIngredient('#', new SimpleItem(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))).addIngredient('I', new SimpleItem(TyreManager.getTyre("intermediate"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("intermediate")))).addIngredient('W', new SimpleItem(TyreManager.getTyre("wet"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("wet")))).addIngredient('S', new SimpleItem(TyreManager.getTyre("soft"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("soft")))).addIngredient('M', new SimpleItem(TyreManager.getTyre("medium"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("medium")))).addIngredient('H', new SimpleItem(TyreManager.getTyre("hard"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("hard")))).build();
    public static void reload() {
        for (Player allCurrentViewer : allTyres.findAllCurrentViewers()) {
            allCurrentViewer.closeInventory();
        }
        allTyres = Gui.normal().setStructure(
                "# # # # # # # # #",
                "# I # S M H # W #",
                "# # # # # # # # #").addIngredient('#', new SimpleItem(Utils.emptyStack(Material.GRAY_STAINED_GLASS_PANE))).addIngredient('I', new SimpleItem(TyreManager.getTyre("intermediate"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("intermediate")))).addIngredient('W', new SimpleItem(TyreManager.getTyre("wet"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("wet")))).addIngredient('S', new SimpleItem(TyreManager.getTyre("soft"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("soft")))).addIngredient('M', new SimpleItem(TyreManager.getTyre("medium"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("medium")))).addIngredient('H', new SimpleItem(TyreManager.getTyre("hard"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("hard")))).build();
    }
}
