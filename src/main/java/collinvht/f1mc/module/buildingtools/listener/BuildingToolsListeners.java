package collinvht.f1mc.module.buildingtools.listener;

import collinvht.f1mc.module.buildingtools.listener.listeners.BlockPlaceListener;
import collinvht.f1mc.module.buildingtools.listener.listeners.ItemsAdderListener;
import collinvht.f1mc.util.modules.ListenerModuleBase;

public class BuildingToolsListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new ItemsAdderListener());
        registerListener(new BlockPlaceListener());
    }
}
