package collinvht.f1mc.module.timetrial;

import collinvht.f1mc.module.timetrial.command.TimeTrialManager;
import collinvht.f1mc.module.timetrial.listener.TimeTrialListener;
import collinvht.f1mc.util.modules.CommandModuleBase;
import org.bukkit.Bukkit;

public class TimeTrialModule extends CommandModuleBase {
    private static TimeTrialManager manager;
    @Override
    public void load() {
        attachModule(new TimeTrialListener());
        manager = new TimeTrialManager();
        registerCommand("timetrial", manager, manager);
    }

    @Override
    public void saveModule() {
        TimeTrialManager.getSessionHashMap().forEach((uuid, timeTrialSession) -> {
            if(Bukkit.getPlayer(timeTrialSession.getPlayer().getUniqueId()) != null) {
                timeTrialSession.getSpawnedVehicle().getStorageVehicle().removeVehicle(Bukkit.getPlayer(timeTrialSession.getPlayer().getUniqueId()));
                timeTrialSession.getPlayer().teleport(timeTrialSession.getPrevLoc());
            }
        });
        manager.unload();
    }
}
