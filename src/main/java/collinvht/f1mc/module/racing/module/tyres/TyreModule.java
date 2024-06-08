package collinvht.f1mc.module.racing.module.tyres;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.tyres.commands.TyreCommands;
import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.util.modules.ModuleBase;

public class TyreModule extends ModuleBase {
    @Override
    public void load() {
        F1MC.getLog().info("[F1MC] [Racing] Enabling Tyre Module");
        attachModule(new TyreManager());
        attachModule(new TyreCommands());
        F1MC.getLog().info("[F1MC] [Racing] Enabled Tyre Module");
    }
}
