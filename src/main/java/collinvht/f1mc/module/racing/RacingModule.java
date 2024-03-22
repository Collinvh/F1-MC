package collinvht.f1mc.module.racing;

import collinvht.f1mc.module.racing.command.RacingCommands;
import collinvht.f1mc.module.racing.listener.RacingListeners;
import collinvht.f1mc.module.racing.manager.RacingManagers;
import collinvht.f1mc.module.racing.module.fia.FiaModule;
import collinvht.f1mc.util.modules.ModuleBase;

public class RacingModule extends ModuleBase {
    @Override
    public void load() {
        attachModule(new RacingCommands());
        attachModule(new RacingManagers());
        attachModule(new FiaModule());
        attachModule(new RacingListeners());
    }
}
