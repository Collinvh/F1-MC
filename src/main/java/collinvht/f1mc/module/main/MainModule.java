package collinvht.f1mc.module.main;

import collinvht.f1mc.module.main.command.MainCommands;
import collinvht.f1mc.module.main.listener.MainListeners;
import collinvht.f1mc.util.modules.ModuleBase;

public class MainModule extends ModuleBase {
    @Override
    public void load() {
        attachModule(new MainCommands());
        attachModule(new MainListeners());
    }

    @Override
    public void saveModule() {

    }
}
