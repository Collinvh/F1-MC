package collinvht.f1mc.module.racing.listener;

import collinvht.f1mc.module.racing.listener.listeners.RacingPlayerListener;
import collinvht.f1mc.util.modules.ListenerModuleBase;

public class RacingListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new RacingPlayerListener());
    }
}
