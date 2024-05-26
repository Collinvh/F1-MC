package collinvht.f1mc.module.vehiclesplus;

import collinvht.f1mc.module.vehiclesplus.listener.VPListeners;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.utils.VehicleInitializer;
import collinvht.f1mc.util.modules.ModuleBase;
import org.bukkit.Bukkit;

public class VehiclePlusModule extends ModuleBase {
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [VPP] Enabling VPP Module");
        attachModule(new VPListeners());
        attachModule(new VehicleInitializer());
        Bukkit.getLogger().info("[F1MC] [VPP] Enabled VPP Module");
    }

    @Override
    public void saveModule() {
        VPListener.getRACE_DRIVERS().forEach((uuid, driver) -> driver.delete());
    }
}
