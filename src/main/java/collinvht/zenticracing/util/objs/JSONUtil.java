package collinvht.zenticracing.util.objs;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.racing.RaceManager;
import collinvht.zenticracing.commands.racing.setup.SetupManager;
import collinvht.zenticracing.commands.team.Team;
import collinvht.zenticracing.commands.team.TeamBaan;
import collinvht.zenticracing.commands.util.MuteUtil;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.IOException;

public class JSONUtil {

    @Getter
    private static final Gson gson = new Gson();

    public static void init(ZenticRacing racing) {
    }


    public static void load() {
        RaceManager.loadRaces();
        Team.loadTeams();
        TeamBaan.loadRaces();
        MuteUtil.saveUtil();
        SetupManager.loadSetups();
    }


    public static void unload() {
        TeamBaan.saveRaces();
        RaceManager.saveRaces();
        MuteUtil.loadUtil();
        Team.saveTeams();
        SetupManager.saveSetups();
    }

}
