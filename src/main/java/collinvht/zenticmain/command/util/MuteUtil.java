package collinvht.zenticmain.command.util;

import collinvht.zenticmain.ZenticMain;
import collinvht.zenticmain.obj.MutedOBJ;
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

    private static final HashMap<UUID, MutedOBJ> mutedObjs = new HashMap<>();

    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            MutedOBJ obj = mutedObjs.get(((Player) sender).getUniqueId());
            if(obj == null) {
                obj = new MutedOBJ(((Player) sender).getUniqueId());
                mutedObjs.put(((Player) sender).getUniqueId(), obj);
            }

            if(args.length > 0) {
                switch (args[0]) {
                    case "word":
                        if(args.length > 1) {
                            if(obj.getWords().contains(args[1].toLowerCase())) {
                                obj.getWords().remove(args[1].toLowerCase());
                                sender.sendMessage(zentic + "Woord geremoved");
                            } else {
                                obj.getWords().add(args[1].toLowerCase());
                                sender.sendMessage(zentic + "Woord geadd!");
                            }
                        } else {
                            sender.sendMessage(zentic + "Vul een woord in.");
                        }
                        return true;
                    case "chat":
                        if(obj.isChatMuted()) {
                            obj.setChatMuted(false);
                            sender.sendMessage(zentic + "Chat geunmute.");
                        } else {
                            obj.setChatMuted(true);
                            sender.sendMessage(zentic + "Chat gemute");
                        }

                        return true;
                    case "player":
                        if(args.length > 1) {
                            Player player = Bukkit.getPlayer(args[1]);
                            if(player != null) {
                                if(obj.getPlayers().contains(player)) {
                                    obj.getPlayers().remove(player);
                                    sender.sendMessage(zentic + "Speler geunmute");
                                } else {
                                    obj.getPlayers().add(player);
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

        for (MutedOBJ obj : mutedObjs.values()) {
            if(obj.isChatMuted()) {
                Player p = obj.getPlayer();
                if(p != null) {
                    playerSet.remove(p);
                }
                return;
            }

            for(UUID uuid : obj.getPlayers()) {
                if(obj.getPlayers().contains(player.getUniqueId())) {
                    Player p = Bukkit.getPlayer(uuid);
                    assert p != null;
                    playerSet.remove(p);
                }
            }

            for (String word : obj.getWords()) {
                if(message.contains(word)) {
                    StringBuilder stars = new StringBuilder();
                    for(int i = 0; i<word.length(); i++) {
                        stars.append("*");
                    }

                    player = obj.getPlayer();

                    if(player != null) {
                        playerSet.remove(player);
                        player.sendMessage(event.getFormat().replace("%1$s", player.getDisplayName()).replace("%2$s", "") + message.replace(word, stars.toString()));
                    }
                }
            }
        }
    }

    public static void saveUtil() {
        File racesLoc = Paths.get(ZenticMain.getInstance().getDataFolder().toString() + "/muteutil" + ".json").toFile();
        File path = Paths.get(ZenticMain.getInstance().getDataFolder().toString()).toFile();
        JsonObject main = new JsonObject();
        JsonArray mainArray = new JsonArray();

        mutedObjs.forEach((uuid, mutedOBJ) ->  {
            JsonObject object = new JsonObject();
            object.addProperty("uuid", uuid.toString());
            JsonArray words = new JsonArray();
            mutedOBJ.getWords().forEach(words::add);

            JsonArray uuids = new JsonArray();
            mutedOBJ.getPlayers().forEach(uuid1 -> uuids.add(uuid1.toString()));

            object.add("MutedWords", words);
            object.add("MutedUUIDs", uuids);

            mainArray.add(object);
        });


        main.add("Players", mainArray);

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
                JsonArray players = jsonObject.getAsJsonArray("Players");

                players.forEach(jsonElement -> {
                    JsonObject object = jsonElement.getAsJsonObject();
                    UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                    MutedOBJ obj = new MutedOBJ(uuid);

                    ArrayList<String> words = new ArrayList<>();
                    JsonArray array = object.getAsJsonArray("MutedWords");
                    array.forEach(jsonElement1 -> {
                        words.add(jsonElement1.getAsString());
                    });

                    obj.setWords(words);

                    ArrayList<UUID> playerz = new ArrayList<>();
                    JsonArray array2 = object.getAsJsonArray("MutedUUIDs");
                    array2.forEach(jsonElement1 -> {
                        playerz.add(UUID.fromString(jsonElement1.getAsString()));
                    });

                    obj.setPlayers(playerz);

                    mutedObjs.put(uuid, obj);
                });

            }
        }
    }
}
