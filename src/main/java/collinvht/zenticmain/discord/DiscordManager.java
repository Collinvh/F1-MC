package collinvht.zenticmain.discord;

import collinvht.zenticmain.ZenticMain;
import lombok.Getter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.security.auth.login.LoginException;

public class DiscordManager extends ListenerAdapter implements Listener {
    @Getter
    private static JDA jda;

    @Getter
    private static TextChannel warningChannel;

    public DiscordManager() {

        startBot();
        jda.addEventListener(this);
    }


    public void startBot() {
        try {
            jda = JDABuilder.createDefault("ODQ0MTMyMDEzNjkwMDYwODQx.YKN9Mw.JR3su1AHEvYRZjMua92LJ1xsz9g").build();
            jda.awaitReady();

            jda.getPresence().setActivity(Activity.streaming("ZenticTwitch", "https://www.twitch.tv/zentictwitch"));

            warningChannel = jda.getTextChannelById(844159011666526208L);
        } catch (InterruptedException | LoginException e ) {
            e.printStackTrace();
        }
    }
}
