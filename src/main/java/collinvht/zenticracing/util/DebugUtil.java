package collinvht.zenticracing.util;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class DebugUtil {
    private static final String prefix = ChatColor.DARK_RED + "DEBUG >>> ";
    private static final ArrayList<UUID> debugging = new ArrayList<>();

    public static void debugMessage(String message) {
        debugging.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                debugging.remove(uuid);
            } else {
                player.sendMessage(prefix + message);
            }
        });
    }

    public static void addDebuggingPlayer(Player player) {
        debugging.add(player.getUniqueId());
    }

    public static void removeDebuggingPlayer(Player player) {
        debugging.remove(player.getUniqueId());
    }

    public static boolean containsPlayer(Player player) {
        return debugging.contains(player.getUniqueId());
    }
}
