package collinvht.projectr.module.main.listener;

import collinvht.projectr.module.main.listener.listeners.MainPlayerListener;
import collinvht.projectr.module.main.listener.listeners.MainVehicleListener;
import collinvht.projectr.util.modules.ListenerModuleBase;

public class MainListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new MainVehicleListener());
        registerListener(new MainPlayerListener());
    }
}
