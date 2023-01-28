package collinvht.projectr.util.modules;

import collinvht.projectr.ProjectR;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class ListenerModuleBase extends ModuleBase {
    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, ProjectR.getInstance());
    }

    @Override
    public final void saveModule() {}
}
