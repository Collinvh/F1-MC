package collinvht.f1mc.module.main;

import collinvht.f1mc.module.main.command.MainCommands;
import collinvht.f1mc.module.main.command.managers.CountryManager;
import collinvht.f1mc.module.main.listener.MainListeners;
import collinvht.f1mc.module.racing.util.RacingMessages;
import collinvht.f1mc.util.modules.ModuleBase;
import org.bukkit.Bukkit;

public class MainModule extends ModuleBase {
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [Main] Enabling Main Module");
        attachModule(new MainCommands());
        attachModule(new CountryManager());
        attachModule(new MainListeners());
        Bukkit.getLogger().info("[F1MC] [Main] Enabled Main Module");
    }

    @Override
    public void saveModule() {

    }
}
