package collinvht.zenticracing.util.objs;

import collinvht.zenticracing.ZenticRacing;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.util.List;

public class DiscordUtil {

    @Getter
    private static JDA jda;

    public static void init(ZenticRacing racing) {
        try {
            jda = JDABuilder.createDefault("ODQ0MTMyMDEzNjkwMDYwODQx.YKN9Mw.JR3su1AHEvYRZjMua92LJ1xsz9g").build();
            jda.awaitReady();

            jda.getPresence().setActivity(Activity.streaming("ZenticTwitch", "https://www.twitch.tv/zentictwitch"));

        } catch (LoginException | InterruptedException ignored) {
        }
    }


    public static TextChannel getChannelByID(long id) {
        return jda.getTextChannelById(id);
    }

    public static List<TextChannel> getChannelsByName(String name) {
        return jda.getTextChannelsByName(name, true);
    }

    public static void setPresence(OnlineStatus status, Activity activity) {
        jda.getPresence().setPresence(status, activity);
    }
}
