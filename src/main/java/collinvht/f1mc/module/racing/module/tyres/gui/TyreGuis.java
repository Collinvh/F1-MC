package collinvht.f1mc.module.racing.module.tyres.gui;

import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.module.racing.module.tyres.obj.TyreClickItem;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class TyreGuis {

    public static final Gui allTyres = Gui.normal().setStructure(
            "# # # # # # # # #",
            "# # S # M # H # #",
            "# # # # # # # # #").addIngredient('S', new SimpleItem(TyreManager.getTyre("soft"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("soft")))).addIngredient('M', new SimpleItem(TyreManager.getTyre("medium"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("medium")))).addIngredient('H', new SimpleItem(TyreManager.getTyre("hard"), click -> click.getPlayer().getInventory().addItem(TyreManager.getTyre("hard")))).build();
}
