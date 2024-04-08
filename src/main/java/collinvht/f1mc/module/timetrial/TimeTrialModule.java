package collinvht.f1mc.module.timetrial;

import collinvht.f1mc.module.timetrial.command.TimeTrialManager;
import collinvht.f1mc.module.timetrial.listener.TimeTrialListener;
import collinvht.f1mc.util.modules.CommandModuleBase;
import org.bukkit.Bukkit;

public class TimeTrialModule extends CommandModuleBase {
    @Override
    public void load() {
        attachModule(new TimeTrialListener());
        registerCommand("timetrial", new TimeTrialManager());
    }

    @Override
    public void saveModule() {
        TimeTrialManager.getSessionHashMap().forEach((uuid, timeTrialSession) -> {
            if(Bukkit.getPlayer(timeTrialSession.getPlayer().getUniqueId()) != null) {
                timeTrialSession.getSpawnedVehicle().getStorageVehicle().removeVehicle(Bukkit.getPlayer(timeTrialSession.getPlayer().getUniqueId()));
                Bukkit.getPlayer(timeTrialSession.getPlayer().getUniqueId()).teleport(timeTrialSession.getPrevLoc());
                timeTrialSession.stop();
            }
        });
    }
}
