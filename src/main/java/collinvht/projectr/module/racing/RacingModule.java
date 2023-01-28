package collinvht.projectr.module.racing;

import collinvht.projectr.module.racing.command.RacingCommands;
import collinvht.projectr.module.racing.manager.RacingManagers;
import collinvht.projectr.util.modules.ModuleBase;

public class RacingModule extends ModuleBase {
    @Override
    public void load() {
        attachModule(new RacingCommands());
        attachModule(new RacingManagers());
    }

    @Override
    public void saveModule() {

    }
}
