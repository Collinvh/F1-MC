package collinvht.f1mc.module.main.gui;


import collinvht.f1mc.module.main.command.managers.CountryManager;
import org.bukkit.Material;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.ScrollItem;

public class CountryGUIs {
    private static final Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r"));
    public static Gui getCountryGUI() {
        ScrollItem ScrollDownItem = new ScrollItem(4) {
            @Override
            public ItemProvider getItemProvider(ScrollGui<?> scrollGui) {
                ItemBuilder builder = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE);
                builder.setDisplayName("§7Scroll down");
                if (!getGui().canScroll(1)) {
                    builder.addLoreLines("§cYou can't scroll further down");
                    builder.setMaterial(Material.RED_STAINED_GLASS_PANE);
                } else {
                    builder.setMaterial(Material.GREEN_STAINED_GLASS_PANE);
                }
                return builder;
            }
        };

        ScrollItem ScrollUpItem = new ScrollItem(-4) {
            @Override
            public ItemProvider getItemProvider(ScrollGui<?> scrollGui) {
                ItemBuilder builder = new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
                builder.setDisplayName("§7Scroll up");
                if (!getGui().canScroll(-1)) {
                    builder.addLoreLines("§cYou've reached the top");
                    builder.setMaterial(Material.RED_STAINED_GLASS_PANE);
                } else {
                    builder.setMaterial(Material.GREEN_STAINED_GLASS_PANE);
                }
                return builder;
            }
        };

        return ScrollGui.items()
                .setStructure(
                        "x x x x x x x x u",
                        "x x x x x x x x #",
                        "x x x x x x x x #",
                        "x x x x x x x x #",
                        "x x x x x x x x d")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('#', border)
                .addIngredient('u', ScrollUpItem)
                .addIngredient('d', ScrollDownItem)
                .setContent(CountryManager.getItems())
                .build();
    }
}
