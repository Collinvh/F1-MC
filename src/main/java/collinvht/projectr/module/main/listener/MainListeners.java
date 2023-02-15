package collinvht.projectr.module.main.listener;

import collinvht.projectr.module.main.listener.listeners.MainPlayerListener;
import collinvht.projectr.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.projectr.util.modules.ListenerModuleBase;

public class MainListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new VPListener());
        registerListener(new MainPlayerListener());
    }
}
