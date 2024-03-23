package collinvht.f1mc.module.racing.module.slowdown;

import collinvht.f1mc.module.racing.module.slowdown.commands.SlowdownCommand;
import collinvht.f1mc.module.racing.module.slowdown.manager.SlowdownManager;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class SlowdownModule extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("slowdown", new SlowdownCommand());
        attachModule(new SlowdownManager());
    }
}
