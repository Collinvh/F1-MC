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
            jda = JDABuilder.createDefault("MTA1MDU0MjgzOTA4OTYwNjcyNw.Gs5wOm.POiVmZVe-nOEStsSCQeh-CvGToZm7VXGSLS-Uk").build();
            jda.awaitReady();

            jda.getPresence().setActivity(Activity.streaming("F1MC", "https://discord.gg/ykXmbNgA7X"));
        } catch (LoginException | InterruptedException ignored) {
            Bukkit.getLogger().warning("Error whilst initializing discord hook, disabling this part of the plugin");
        }
    }

    @Override
    public void saveModule() {
        jda.shutdown();
    }
}
