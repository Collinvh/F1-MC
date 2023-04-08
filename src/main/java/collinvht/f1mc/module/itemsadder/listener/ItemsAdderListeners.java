package collinvht.f1mc.module.itemsadder.listener;

import collinvht.f1mc.module.itemsadder.listener.listeners.ItemsAdderListener;
import collinvht.f1mc.util.modules.ListenerModuleBase;

public class ItemsAdderListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new ItemsAdderListener());
    }
}
