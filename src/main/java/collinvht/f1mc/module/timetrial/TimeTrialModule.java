package collinvht.f1mc.module.timetrial;

import collinvht.f1mc.module.timetrial.command.TimeTrialCommands;
import collinvht.f1mc.module.timetrial.listener.TimeTrialListener;
import collinvht.f1mc.module.timetrial.manager.TimeTrialManager;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class TimeTrialModule extends CommandModuleBase {
    @Override
    public void load() {
        TimeTrialManager.load();
        attachModule(new TimeTrialCommands());
        attachModule(new TimeTrialListener());
    }

    @Override
    public void saveModule() {
        TimeTrialManager.unload();
    }
}
