package collinvht.f1mc.module.racing.module.fia;

import collinvht.f1mc.module.racing.module.fia.command.FiaCommands;
import collinvht.f1mc.util.modules.ModuleBase;
import org.bukkit.Bukkit;

public class FiaModule extends ModuleBase {
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [Racing] Enabling FIA Module");
        attachModule(new FiaCommands());
        Bukkit.getLogger().info( "[F1MC] [Racing] Enabled FIA Module");
    }
}
