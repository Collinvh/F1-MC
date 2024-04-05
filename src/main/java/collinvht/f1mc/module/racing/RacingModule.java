package collinvht.f1mc.module.racing;

import collinvht.f1mc.module.racing.command.RacingCommands;
import collinvht.f1mc.module.racing.listener.RacingListeners;
import collinvht.f1mc.module.racing.manager.RacingManagers;
import collinvht.f1mc.module.racing.module.fia.FiaModule;
import collinvht.f1mc.module.racing.module.slowdown.SlowdownModule;
import collinvht.f1mc.module.racing.module.team.TeamModule;
import collinvht.f1mc.module.racing.module.tyres.TyreModule;
import collinvht.f1mc.module.racing.object.race.RaceCar;
import collinvht.f1mc.module.racing.util.RacingMessages;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.util.modules.ModuleBase;
import org.bukkit.Bukkit;

public class RacingModule extends ModuleBase {
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [Racing] Enabling Racing Modules");
        attachModule(new RacingCommands());
        attachModule(new RacingManagers());
        attachModule(new FiaModule());
        attachModule(new RacingListeners());
        attachModule(new SlowdownModule());
        attachModule(new TyreModule());
        attachModule(new TeamModule());
        Bukkit.getLogger().info("[F1MC] [Racing] Enabled Racing Modules");
    }

    @Override
    public void saveModule() {
        for (RaceCar value : VPListener.getRACE_CARS().values()) {
            value.getLinkedVehicle().despawn(true);
        }
    }
}
