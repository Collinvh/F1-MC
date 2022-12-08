package collinvht.projectr.listener;

import collinvht.projectr.ProjectR;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Listeners {

    public static void initializeListeners() {
        addListener(new PlayerListener());
        addListener(new VPPListener());
    }


    private static void addListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, ProjectR.getRacing());
    }
}
