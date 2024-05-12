package collinvht.f1mc.module.racing.module.slowdown;

import collinvht.f1mc.module.racing.module.slowdown.command.SlowdownCommand;
import collinvht.f1mc.module.racing.module.slowdown.manager.SlowdownManager;
import collinvht.f1mc.module.racing.util.RacingMessages;
import collinvht.f1mc.util.modules.CommandModuleBase;
import net.luckperms.api.messenger.message.Message;
import org.bukkit.Bukkit;

public class SlowdownModule extends CommandModuleBase {
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [Racing] Enabling Slowdown Module");
        registerCommand("slowdown", new SlowdownCommand(), new SlowdownCommand());
        attachModule(new SlowdownManager());
        Bukkit.getLogger().info("[F1MC] [Racing] Enabled Slowdown Module");
    }
}
