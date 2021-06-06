package collinvht.zenticracing;

import collinvht.zenticracing.commands.Commands;
import collinvht.zenticracing.listener.Listeners;
import collinvht.zenticracing.listener.VPPListener;
import collinvht.zenticracing.util.Utils;
import collinvht.zenticracing.util.objs.JSONUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZenticRacing extends JavaPlugin {
    @Getter
    private static ZenticRacing racing;

    @Override
    public void onEnable() {
        racing = this;

        Utils.initializeUtils();
        Commands.initializeCommands();
        Listeners.initializeListeners();

        JSONUtil.load();
    }

    @Override
    public void onDisable() {
        JSONUtil.unload();
        VPPListener.cancel();
    }
}
