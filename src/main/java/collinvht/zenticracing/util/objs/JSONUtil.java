package collinvht.zenticracing.util.objs;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.racing.RaceManager;
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
        try {
            RaceManager.loadRaces();
            Team.loadTeams();
            TeamBaan.loadRaces();
            MuteUtil.saveUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void unload() {
        try {
            TeamBaan.saveRaces();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            RaceManager.saveRaces();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MuteUtil.loadUtil();
        Team.saveTeams();
    }

}
