package collinvht.f1mc.module.discord;

import collinvht.f1mc.util.modules.ModuleBase;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;

public class DiscordModule extends ModuleBase {
    @Getter
    private static JDA jda;
    @Override
    public void load() {
        try {
            jda = JDABuilder.createDefault("MTA1MDU0MjgzOTA4OTYwNjcyNw.GdCG1P.VUDZJzQft9ogs2yfvAfJaQ-qB44su_rEzJ4yAs").build();
            jda.awaitReady();
            jda.getPresence().setActivity(Activity.streaming("F1MC", "https://discord.gg/xR3NAbCxJR"));
        } catch (LoginException | InterruptedException ignored) {
            Bukkit.getLogger().warning("Error whilst initializing discord hook, disabling this part of the plugin");
            setInitialized(false);
        }
    }

    @Override
    public void saveModule() {
        jda.shutdown();
    }
}
