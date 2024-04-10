package collinvht.f1mc.module.racing.manager;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.util.modules.ModuleBase;

public class RacingManagers extends ModuleBase {
    @Override
    public void load() {
        attachModule(new RaceManager());
    }

    @Override
    public void saveModule() {
        RaceManager.getRACES().forEach((s, race) -> race.deleteLeaderboard());
    }
}
