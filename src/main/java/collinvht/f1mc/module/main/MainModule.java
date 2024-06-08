package collinvht.f1mc.module.main;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.main.command.MainCommands;
import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.module.main.listener.MainListeners;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;

public class MainModule extends ModuleBase {
    @Override
    public void load() {
        F1MC.getLog().info("[F1MC] [Main] Enabling Main Module");
        attachModule(new MainCommands());
        if(Utils.isEnableDiscordModule()) {
            attachModule(new CountryManager());
        }
        attachModule(new MainListeners());
        F1MC.getLog().info("[F1MC] [Main] Enabled Main Module");
    }
}
