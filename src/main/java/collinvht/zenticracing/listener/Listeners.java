package collinvht.zenticracing.listener;

import collinvht.zenticracing.ZenticRacing;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Listeners {

    public static void initializeListeners() {
        addListener(new PlayerListener());
        addListener(new VPPListener());
    }


    private static void addListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, ZenticRacing.getRacing());
    }
}
