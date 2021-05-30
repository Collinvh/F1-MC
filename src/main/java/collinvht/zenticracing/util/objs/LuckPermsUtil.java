package collinvht.zenticracing.util.objs;

import collinvht.zenticracing.ZenticRacing;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsUtil {
    @Getter
    private static LuckPerms luckPerms;

    public static void init(ZenticRacing racing) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        } else {
            Bukkit.getPluginManager().disablePlugin(racing);
        }
    }


}
