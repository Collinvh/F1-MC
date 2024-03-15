package collinvht.f1mc.module.racing.module.fia;

import collinvht.f1mc.module.racing.module.fia.command.FiaCommands;
import collinvht.f1mc.util.modules.ModuleBase;

public class FiaModule extends ModuleBase {
    @Override
    public void load() {
        attachModule(new FiaCommands());
    }
}
