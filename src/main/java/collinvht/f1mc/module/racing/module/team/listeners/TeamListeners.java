package collinvht.f1mc.module.racing.module.team.listeners;


import collinvht.f1mc.module.racing.module.team.listeners.listener.InteractListener;
import collinvht.f1mc.module.racing.module.team.object.PCGui;
import collinvht.f1mc.util.modules.ListenerModuleBase;

public class TeamListeners extends ListenerModuleBase {
    @Override
    public void load() {
        registerListener(new InteractListener());
        registerListener(new PCGui());
    }
}
