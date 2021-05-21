package collinvht.zenticmain.command.util;

import collinvht.zenticmain.ZenticMain;
import collinvht.zenticmain.obj.TeamObj;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static collinvht.zenticmain.command.util.Team.readJson;

public class MuteUtil implements CommandExecutor, Listener {

    private static final HashMap<UUID, ArrayList<Player>> mutedPlayers = new HashMap<>();
    private static final HashMap<UUID, ArrayList<String>> mutedWords = new HashMap<>();
    private static final ArrayList<Player> hasChatMuted = new ArrayList<>();

    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            ArrayList<Player> mutedPlayers = MuteUtil.mutedPlayers.get(((Player) sender).getUniqueId());
            ArrayList<String> mutedWords = MuteUtil.mutedWords.get(((Player) sender).getUniqueId());
            if(mutedPlayers == null) {
                mutedPlayers = new ArrayList<>();
                MuteUtil.mutedPlayers.put(((Player) sender).getUniqueId(), mutedPlayers);
            }

            if(mutedWords == null) {
                mutedWords = new ArrayList<>();
                MuteUtil.mutedWords.put(((Player) sender).getUniqueId(), mutedWords);
            }

            if(args.length > 0) {
                switch (args[0]) {
                    case "word":
                        if(args.length > 1) {
                            if(mutedWords.contains(args[1].toLowerCase())) {
                                mutedWords.remove(args[1].toLowerCase());
                                sender.sendMessage(zentic + "Woord geremoved");
                            } else {
                                mutedWords.add(args[1].toLowerCase());
                                sender.sendMessage(zentic + "Woord geadd!");
                            }
                        } else {
                            sender.sendMessage(zentic + "Vul een woord in.");
                        }
                        return true;
                    case "chat":
                        if(hasChatMuted.contains(((Player) sender).getPlayer())) {
                            hasChatMuted.remove(((Player) sender).getPlayer());
                            sender.sendMessage(zentic + "Chat geunmute.");
                        } else {
                            hasChatMuted.add(((Player) sender).getPlayer());
                            sender.sendMessage(zentic + "Chat gemute");
                        }

                        return true;
                    case "player":
                        if(args.length > 1) {
                            Player player = Bukkit.getPlayer(args[1]);
                            if(player != null) {
                                if(mutedPlayers.contains(player)) {
                                    mutedPlayers.remove(player);
                                    sender.sendMessage(zentic + "Speler geunmute");
                                } else {
                                    mutedPlayers.add(player);
                                    sender.sendMessage(zentic + "Speler gemute");
                                }
                            }
                        }
                        return true;
                    default:
                        sender.sendMessage(zentic + "Usage \n /muteutil word [woord] \n /muteutil chat \n /muteutil player [player]");
                        return true;
                }
            } else {
                sender.sendMessage(zentic + "Usage \n /muteutil word [woord] \n /muteutil chat \n /muteutil player [player]");
                return true;
            }

        }

        return false;
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        Set<Player> playerSet = event.getRecipients();

        for(Player p : hasChatMuted) {
            playerSet.remove(p);
            return;
        }


        for(UUID uuid : mutedPlayers.keySet()) {
            ArrayList<Player> players = mutedPlayers.get(uuid);
            ArrayList<String> woorden = mutedWords.get(uuid);
            if(players.contains(player)) {
                Player p = Bukkit.getPlayer(uuid);
                assert p != null;
                playerSet.remove(p);
            }

            for(String woord : woorden) {
                if(message.contains(woord)) {
                    StringBuilder stars = new StringBuilder();
                    for(int i = 0; i<woord.length(); i++) {
                        stars.append("*");
                    }
                    Player p = Bukkit.getPlayer(uuid);
                    if(p != null) {
                        playerSet.remove(p);
                        p.sendMessage(event.getFormat().replace("%1$s", p.getDisplayName()).replace("%2$s", "") + message.replace(woord, stars.toString()));
                    }
                }
            }
        }
    }

    public static void saveUtil() {
        File racesLoc = Paths.get(ZenticMain.getInstance().getDataFolder().toString() + "/muteutil" + ".json").toFile();
        File path = Paths.get(ZenticMain.getInstance().getDataFolder().toString()).toFile();
        JsonObject main = new JsonObject();
        JsonObject muted = new JsonObject();
        JsonObject mutedWords = new JsonObject();

        mutedPlayers.forEach((uuid, players) -> {
            JsonArray playerArray = new JsonArray();
            players.forEach(player ->  {
                JsonObject object = new JsonObject();
                object.addProperty("uuid", player.getUniqueId().toString());
                playerArray.add(object);
            });

            muted.add(uuid.toString(), playerArray);
        });

        MuteUtil.mutedWords.forEach((uuid, strings) -> {
            JsonArray playerArray = new JsonArray();
            strings.forEach(player ->  {
                JsonObject object = new JsonObject();
                object.addProperty("word", player);
                playerArray.add(object);
            });

            mutedWords.add(uuid.toString(), playerArray);
        });

        main.add("MutedPlayers", muted);
        main.add("MutedWords", mutedWords);

        try {
            if (path.mkdir() || racesLoc.createNewFile() || racesLoc.exists()) {
                FileWriter writer = new FileWriter(racesLoc);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                writer.write(gson.toJson(main));
                writer.flush();
            }
        } catch (IOException ignored) {
        }
    }

    public static void loadUtil() {
        File teamLoc = Paths.get(ZenticMain.getInstance().getDataFolder().toString() + "/muteutil" + ".json").toFile();
        if(teamLoc.exists()) {
            JsonObject jsonObject = null;
            try {
                jsonObject = (JsonObject) readJson(ZenticMain.getInstance().getDataFolder().toString() + "/muteutil" + ".json");
            } catch (Exception ignored) {
            }

            if(jsonObject != null) {
                JsonObject players = jsonObject.getAsJsonObject("MutedPlayers");
                JsonObject words = jsonObject.getAsJsonObject("MutedWords");
            }
        }
    }
}
