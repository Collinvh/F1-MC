package collinvht.f1mc.module.racing.module.slowdown;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.slowdown.command.SlowdownCommand;
import collinvht.f1mc.module.racing.module.slowdown.manager.SlowdownManager;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class SlowdownModule extends CommandModuleBase {
    @Override
    public void load() {
        F1MC.getLog().info("[F1MC] [Racing] Enabling Slowdown Module");
        registerCommand("slowdown", new SlowdownCommand(), new SlowdownCommand());
        attachModule(new SlowdownManager());
        F1MC.getLog().info("[F1MC] [Racing] Enabled Slowdown Module");
    }
}
