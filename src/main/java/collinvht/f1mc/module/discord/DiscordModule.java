package collinvht.f1mc.module.discord;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.util.RacingMessages;
import collinvht.f1mc.util.modules.ModuleBase;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import javax.security.auth.login.LoginException;

public class DiscordModule extends ModuleBase {
    @Getter
    private JDA jda;
    @Getter
    private static DiscordModule instance;
    @Override
    public void load() {
        Bukkit.getLogger().info("[F1MC] [Discord] Enabling Discord Module");
        instance = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    jda = JDABuilder.createDefault("MTA1MDU0MjgzOTA4OTYwNjcyNw.GdCG1P.VUDZJzQft9ogs2yfvAfJaQ-qB44su_rEzJ4yAs").build();
                    jda.awaitReady();
                    jda.getPresence().setActivity(Activity.watching("https://discord.gg/xR3NAbCxJR"));
                    Bukkit.getLogger().info("[F1MC] [Discord] Enabled Discord Module");
                } catch (LoginException | InterruptedException ignored) {
                    Bukkit.getLogger().warning("Error whilst initializing discord hook, disabling this part of the plugin");
                    setInitialized(false);
                }
            }
        }.runTaskAsynchronously(F1MC.getInstance());
    }

    @Override
    public void saveModule() {
        jda.shutdown();
    }
}
