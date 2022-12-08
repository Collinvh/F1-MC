package collinvht.projectr.util.objs;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.racing.RaceManager;
import collinvht.projectr.commands.racing.setup.SetupManager;
import collinvht.projectr.commands.team.Team;
import collinvht.projectr.commands.util.MuteUtil;
import com.google.gson.Gson;
import lombok.Getter;

public class JSONUtil {

    @Getter
    private static final Gson gson = new Gson();

    public static void init(ProjectR racing) {
    }


    public static void load() {
        RaceManager.loadRaces();
        Team.loadTeams();
        MuteUtil.saveUtil();
        SetupManager.loadSetups();
    }


    public static void unload() {
        RaceManager.saveRaces();
        MuteUtil.loadUtil();
        Team.saveTeams();
        SetupManager.saveSetups();
    }

}
