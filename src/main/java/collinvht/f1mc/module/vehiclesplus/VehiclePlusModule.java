package collinvht.f1mc.module.vehiclesplus;

import collinvht.f1mc.module.vehiclesplus.listener.VPListeners;
import collinvht.f1mc.module.vehiclesplus.utils.VehicleInitializer;
import collinvht.f1mc.util.modules.ModuleBase;

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
