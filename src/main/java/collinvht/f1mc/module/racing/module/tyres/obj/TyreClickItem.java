package collinvht.f1mc.module.racing.module.tyres.obj;

import collinvht.f1mc.module.racing.object.race.RaceCarGUI;
import collinvht.f1mc.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class TyreClickItem extends SimpleItem {
    private final RaceCarGUI car;
    private final Inventory next;
    public TyreClickItem(RaceCarGUI raceCar, ItemStack stack, Inventory nextInv) {
        super(stack == null ? new ItemStack(Material.AIR) : stack);
        this.car = raceCar;
        this.next = nextInv;
    }

    private int count = 0;
    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if(event.isShiftClick()) return;
        if(clickType.isRightClick() || clickType.isLeftClick()) {
            if (count == 2) {
                if (next.getItem(0).getType() == Material.RED_STAINED_GLASS_PANE) {
                    count = 0;
                }
            }

            if (count < 2) {
                count += 1;
                if (count == 1) {
                    next.forceSetItem(UpdateReason.SUPPRESSED, 0, Utils.emptyStack(Material.ORANGE_STAINED_GLASS_PANE));
                } else if (count == 2) {
                    next.forceSetItem(UpdateReason.SUPPRESSED, 0, Utils.emptyStack(Material.LIME_STAINED_GLASS_PANE));
                    car.checkIfComplete();
                }
            }
        }
    }
}
