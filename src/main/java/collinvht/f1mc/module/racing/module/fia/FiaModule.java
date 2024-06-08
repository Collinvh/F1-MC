package collinvht.f1mc.module.racing.module.fia;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.fia.command.FiaCommands;
import collinvht.f1mc.util.modules.ModuleBase;

public class FiaModule extends ModuleBase {
    @Override
    public void load() {
        F1MC.getLog().info("[F1MC] [Racing] Enabling FIA Module");
        attachModule(new FiaCommands());
        F1MC.getLog().info( "[F1MC] [Racing] Enabled FIA Module");
    }
}
