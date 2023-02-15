package collinvht.projectr.module.vehiclesplus;

import collinvht.projectr.module.vehiclesplus.listener.VPListeners;
import collinvht.projectr.module.vehiclesplus.utils.VehicleInitializer;
import collinvht.projectr.util.modules.ModuleBase;

public class VehiclePlusModule extends ModuleBase {
    @Override
    public void load() {
        attachModule(new VPListeners());
        attachModule(new VehicleInitializer());
    }

    @Override
    public void saveModule() {

    }
}
