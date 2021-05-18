package collinvht.zenticmain.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface CommandUtil {

    default void sendMessageToServer(String mesage) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(mesage);
        }
    }
}
