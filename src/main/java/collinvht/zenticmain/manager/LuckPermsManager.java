package collinvht.zenticmain.manager;

import collinvht.zenticmain.ZenticMain;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsManager {

    private static LuckPerms api;

    public static LuckPerms getApi() {
        if(api != null) {
            return api;
        } else {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                api = provider.getProvider();
                return api;
            } else {
                Bukkit.getPluginManager().disablePlugin(ZenticMain.getInstance());
            }
        }
        return null;
    }
}
