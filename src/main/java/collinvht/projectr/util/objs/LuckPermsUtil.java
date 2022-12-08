package collinvht.projectr.util.objs;

import collinvht.projectr.ProjectR;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsUtil {
    @Getter
    private static LuckPerms luckPerms;

    public static void init(ProjectR racing) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        } else {
            Bukkit.getPluginManager().disablePlugin(racing);
        }
    }


}
