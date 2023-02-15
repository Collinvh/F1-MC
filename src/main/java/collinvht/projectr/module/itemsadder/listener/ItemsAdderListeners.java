package collinvht.projectr.module.itemsadder.listener;

import collinvht.projectr.module.itemsadder.listener.listeners.ItemsAdderListener;
import collinvht.projectr.util.modules.ListenerModuleBase;

public class ItemsAdderListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new ItemsAdderListener());
    }
}
