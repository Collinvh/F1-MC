package collinvht.f1mc.module.itemsadder;

import collinvht.f1mc.module.itemsadder.listener.ItemsAdderListeners;
import collinvht.f1mc.util.modules.ModuleBase;

public class ItemsAdderModule extends ModuleBase {
    @Override
    public void load() {
        attachModule(new ItemsAdderListeners());
    }

    @Override
    public void saveModule() {

    }
}
