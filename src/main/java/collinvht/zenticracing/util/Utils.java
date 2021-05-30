package collinvht.zenticracing.util;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.util.objs.DiscordUtil;
import collinvht.zenticracing.util.objs.JSONUtil;
import collinvht.zenticracing.util.objs.LuckPermsUtil;
import collinvht.zenticracing.util.objs.WorldEditUtil;

public class Utils {
    private static final ZenticRacing racing = ZenticRacing.getRacing();

    public static void initializeUtils() {
        DiscordUtil.init(racing);
        JSONUtil.init(racing);
        LuckPermsUtil.init(racing);
        WorldEditUtil.init(racing);
    }
}
