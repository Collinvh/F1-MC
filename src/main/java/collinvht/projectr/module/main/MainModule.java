package collinvht.projectr.module.main;

import collinvht.projectr.module.main.command.MainCommands;
import collinvht.projectr.module.main.listener.MainListeners;
import collinvht.projectr.util.modules.ModuleBase;

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
