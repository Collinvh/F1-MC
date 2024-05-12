package collinvht.f1mc.module.racing.module.tyres.listeners;

import collinvht.f1mc.module.racing.module.tyres.listeners.listener.TyreGUI;
import collinvht.f1mc.util.modules.ListenerModuleBase;

public class TyreListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new TyreGUI());
    }
}
