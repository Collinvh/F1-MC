package collinvht.projectr.util;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.objs.DiscordUtil;
import collinvht.projectr.util.objs.JSONUtil;
import collinvht.projectr.util.objs.LuckPermsUtil;
import collinvht.projectr.util.objs.WorldEditUtil;

public class Utils {
    private static final ProjectR racing = ProjectR.getRacing();

    public static void initializeUtils() {
        DiscordUtil.init(racing);
        JSONUtil.init(racing);
        LuckPermsUtil.init(racing);
        WorldEditUtil.init(racing);
    }
}
