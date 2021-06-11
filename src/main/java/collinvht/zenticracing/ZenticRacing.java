package collinvht.zenticracing;

import collinvht.zenticracing.commands.Commands;
import collinvht.zenticracing.commands.racing.setup.SetupManager;
import collinvht.zenticracing.commands.racing.setup.obj.SetupOBJ;
import collinvht.zenticracing.listener.Listeners;
import collinvht.zenticracing.listener.VPPListener;
import collinvht.zenticracing.util.Utils;
import collinvht.zenticracing.util.objs.JSONUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ZenticRacing extends JavaPlugin {
    @Getter
    private static ZenticRacing racing;

    @Override
    public void onEnable() {
        racing = this;

        Utils.initializeUtils();
        Commands.initializeCommands();
        Listeners.initializeListeners();

        for(Player player : Bukkit.getOnlinePlayers()) {
            SetupManager.createSetupForPlayer(player.getUniqueId());
        }

        JSONUtil.load();
    }

    @Override
    public void onDisable() {
        JSONUtil.unload();
        VPPListener.cancel();
    }
}
