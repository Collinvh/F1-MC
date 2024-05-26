package collinvht.f1mc.module.racing.module.tyres;

import collinvht.f1mc.module.racing.module.tyres.commands.TyreCommands;
import collinvht.f1mc.module.racing.module.tyres.manager.TyreManager;
import collinvht.f1mc.util.modules.ModuleBase;
import org.bukkit.Bukkit;

public class TyreModule extends ModuleBase {
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [Racing] Enabling Tyre Module");
        attachModule(new TyreManager());
        attachModule(new TyreCommands());
        Bukkit.getLogger().info("[F1MC] [Racing] Enabled Tyre Module");
    }
}
