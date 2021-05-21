package collinvht.zenticmain;

import collinvht.zenticmain.command.race.*;
import collinvht.zenticmain.command.util.*;
import collinvht.zenticmain.discord.DiscordManager;
import collinvht.zenticmain.event.VPPEvents;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class ZenticMain extends JavaPlugin {
    @Getter
    private static VPPEvents VPPEventManager;

    @Setter @Getter
    private static String status;

    private static final MuteUtil util = new MuteUtil();

    @Getter
    private static ZenticMain instance;

    @Override
    public void onEnable() {
        instance = this;

        VPPEventManager = new VPPEvents();
        Bukkit.getPluginManager().registerEvents(VPPEventManager, this);
        Bukkit.getPluginManager().registerEvents(new DiscordManager(), this);
        Bukkit.getPluginManager().registerEvents(util, this);

        Team.loadTeams();

        this.getCommand("warning").setExecutor(new Warning());
        this.getCommand("penalty").setExecutor(new Penalty());
        this.getCommand("dsq").setExecutor(new DSQ());
        this.getCommand("flag").setExecutor(new flag());
        this.getCommand("lights").setExecutor(new lights());
        this.getCommand("clearchat").setExecutor(new ClearChat());
        this.getCommand("garage").setExecutor(new SpawnCar());
        this.getCommand("muteutil").setExecutor(new MuteUtil());
        this.getCommand("team").setExecutor(new Team());
    }

    @Override
    public void onDisable() {
        Team.saveTeams();
    }


    public static void LogMessage(String message) {
        Bukkit.getLogger().log(Level.INFO, "ZT >> " + message);
    }
}
