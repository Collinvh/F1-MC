package collinvht.f1mc.module.main.listener;

import collinvht.f1mc.module.main.listener.listeners.MainPlayerListener;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.util.modules.ListenerModuleBase;

public class MainListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new VPListener());
        registerListener(new MainPlayerListener());
    }
}
