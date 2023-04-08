package collinvht.f1mc.util.modules;

import collinvht.f1mc.F1MC;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class ListenerModuleBase extends ModuleBase {
    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, F1MC.getInstance());
    }

    @Override
    public final void saveModule() {}
}
