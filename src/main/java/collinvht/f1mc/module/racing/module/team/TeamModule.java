package collinvht.f1mc.module.racing.module.team;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.team.command.RaceTeamCommand;
import collinvht.f1mc.module.racing.module.team.command.TeamChatCommand;
import collinvht.f1mc.module.racing.module.team.listeners.TeamListeners;
import collinvht.f1mc.module.racing.module.team.manager.TeamManager;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class TeamModule extends CommandModuleBase {
    @Override
    public void load() {
        F1MC.getLog().info("[F1MC] [Racing] Enabling Team Module");
        attachModule(new TeamManager());
        attachModule(new TeamListeners());
        registerCommand("raceteam", new RaceTeamCommand(), new RaceTeamCommand());
        registerCommand("teamchat", new TeamChatCommand());
        F1MC.getLog().info("[F1MC] [Racing] Enabled Team Module");
    }
}
