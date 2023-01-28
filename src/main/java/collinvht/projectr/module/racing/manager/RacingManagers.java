package collinvht.projectr.module.racing.manager;

import collinvht.projectr.module.racing.manager.managers.RaceManager;
import collinvht.projectr.util.modules.ModuleBase;

public class RacingManagers extends ModuleBase {
    @Override
    public void load() {
        attachModule(new RaceManager());
    }

    @Override
    public void saveModule() {}
}
