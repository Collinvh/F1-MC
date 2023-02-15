package collinvht.projectr.module.racing.listener;

import collinvht.projectr.module.racing.listener.listeners.RacingPlayerListener;
import collinvht.projectr.util.modules.ListenerModuleBase;

public class RacingListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new RacingPlayerListener());
    }
}
