package collinvht.f1mc.module.racing.manager;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleCollisionEvent;

public class RacingManagers extends ModuleBase {
    @Override
    public void load() {
        attachModule(new RaceManager());
    }

    @Override
    public void saveModule() {
        Utils.setEnableDiscordModule(false);
        RaceManager.getRACES().forEach((s, race) -> {
            race.deleteLeaderboard();
            RaceManager.getInstance().stopRace(s);
        });
    }
}
