package collinvht.projectr.module.itemsadder;

import collinvht.projectr.module.itemsadder.listener.ItemsAdderListeners;
import collinvht.projectr.util.modules.ModuleBase;

public class ItemsAdderModule extends ModuleBase {
    @Override
    public void load() {
        attachModule(new ItemsAdderListeners());
    }

    @Override
    public void saveModule() {

    }
}
