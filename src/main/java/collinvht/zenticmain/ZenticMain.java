package collinvht.zenticmain;

import collinvht.zenticmain.command.*;
import collinvht.zenticmain.discord.DiscordManager;
import collinvht.zenticmain.event.VPPEvents;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class ZenticMain extends JavaPlugin {
    @Getter
    private static VPPEvents VPPEventManager;

    @Setter @Getter
    private static String status;

    @Override
    public void onEnable() {
        VPPEventManager = new VPPEvents();
        Bukkit.getPluginManager().registerEvents(VPPEventManager, this);
        Bukkit.getPluginManager().registerEvents(new DiscordManager(), this);
        this.getCommand("warning").setExecutor(new Warning());
        this.getCommand("penalty").setExecutor(new Penalty());
        this.getCommand("dsq").setExecutor(new DSQ());
        this.getCommand("flag").setExecutor(new flag());
        this.getCommand("lights").setExecutor(new lights());
    }

    @Override
    public void onDisable() {

    }


    public static void LogMessage(String message) {
        Bukkit.getLogger().log(Level.INFO, "ZT >> " + message);
    }
}
