package collinvht.f1mc.module.vehiclesplus.listener;

import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.util.modules.ListenerModuleBase;

public class VPListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new VPListener());
    }
}
