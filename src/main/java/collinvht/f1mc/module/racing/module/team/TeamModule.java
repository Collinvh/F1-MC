package collinvht.f1mc.module.racing.module.team;

import collinvht.f1mc.module.racing.module.team.command.RaceTeamCommand;
import collinvht.f1mc.module.racing.module.team.listeners.TeamListeners;
import collinvht.f1mc.module.racing.module.team.manager.TeamManager;
import collinvht.f1mc.util.modules.CommandModuleBase;
import org.bukkit.Bukkit;

public class TeamModule extends CommandModuleBase {
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [Racing] Enabling Team Module");
        attachModule(new TeamManager());
        attachModule(new TeamListeners());
        registerCommand("raceteam", new RaceTeamCommand(), new RaceTeamCommand());
        Bukkit.getLogger().info("[F1MC] [Racing] Enabled Team Module");
    }
}
