package collinvht.projectr.module.vehiclesplus.listener;

import collinvht.projectr.module.vehiclesplus.VehiclePlusModule;
import collinvht.projectr.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.projectr.util.modules.ListenerModuleBase;

public class VPListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new VPListener());
    }
}
