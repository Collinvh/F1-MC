package collinvht.zenticracing.commands.team;

import collinvht.zenticracing.ZenticRacing;
import collinvht.zenticracing.commands.CommandUtil;
import collinvht.zenticracing.commands.team.object.TeamObject;
import com.google.gson.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class Team implements CommandUtil, Listener {

    @Getter
    private static final HashMap<String, TeamObject> teamObj = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            TeamObject obj = checkTeamForPlayer(player);
            if (player != null) {
                if (!sender.hasPermission("zentic.team")) {
                    if (obj != null) {
                        if (args.length > 0) {
                            switch (args[0]) {
                                case "add":
                                    if (obj.getOwnerUUID().equals(((Player) sender).getUniqueId())) {
                                        if (args.length > 1) {
                                            Player member = Bukkit.getPlayer(args[1]);
                                            if (member != null) {
                                                TeamObject obj1 = checkTeamForPlayer(member);
                                                if (obj1 != null) {
                                                    if (obj1.getTeamName().equals(obj.getTeamName())) {
                                                        sender.sendMessage(prefix + "Die speler zit al in jouw team!");
                                                    } else {
                                                        sender.sendMessage(prefix + "Die speler zit al in een team!");
                                                    }
                                                } else {
                                                    obj.inviteMember(sender, member.getUniqueId());
                                                }
                                            } else {
                                                sender.sendMessage(prefix + "Die speler bestaat niet!");
                                            }
                                        } else {
                                            sender.sendMessage(prefix + "Usage /raceteam add [spelernaam]");
                                        }
                                    } else {
                                        sender.sendMessage(prefix + "Jij bent geen owner!");
                                    }
                                    return true;
                                case "kick":
                                case "remove":
                                    if (obj.getOwnerUUID().equals(((Player) sender).getUniqueId())) {
                                        if (args.length > 1) {
                                            Player member = Bukkit.getPlayer(args[1]);
                                            if (member != null) {
                                                if (!member.getUniqueId().equals(((Player) sender).getUniqueId())) {
                                                    obj.removeMember(sender, member.getUniqueId());
                                                } else {
                                                    sender.sendMessage(prefix + "Je bent teambaas... Je kunt niet zomaar je team verlaten.");
                                                }
                                            } else {
                                                sender.sendMessage(prefix + "Die speler bestaat niet!");
                                            }
                                        } else {
                                            sender.sendMessage(prefix + "Usage /raceteam remove [spelernaam]");
                                        }
                                    } else {
                                        sender.sendMessage(prefix + "Jij bent geen owner!");
                                    }
                                    return true;
                                case "info":
                                    OfflinePlayer owner = obj.getOwner();
                                    sender.sendMessage(prefix + "Team Info \n " + obj.getTeamName() + " \n" + "Owner : " + owner.getName() + "\n" + "Membercount : " + obj.getTeamMembers().size() + "\n");
                                    return true;
                                case "leave":
                                    if (!obj.getOwnerUUID().equals(((Player) sender).getUniqueId())) {
                                        obj.removeMember(null, ((Player) sender).getUniqueId());
                                        sender.sendMessage(prefix + "Je hebt het team verlaten...");
                                    } else {
                                        sender.sendMessage(prefix + "Je bent teambaas... Je kunt niet zomaar je team verlaten.");
                                    }
                            }
                        } else {
                            sender.sendMessage(prefix + "Usage is \n /raceteam add [spelernaam] \n /raceteam remove [spelernaam] \n /raceteam info \n /raceteam leave");
                        }
                    } else {
                        if (args.length > 1) {
                            if (args[0].equalsIgnoreCase("accept")) {
                                TeamObject team = teamObj.get(args[1].toLowerCase());
                                if (team != null) {
                                    if (team.getInvited().contains(((Player) sender).getUniqueId())) {
                                        team.addMember(null, ((Player) sender).getUniqueId());
                                        sender.sendMessage(prefix + "Team gejoined!");
                                    } else {
                                        sender.sendMessage(prefix + "Je bent niet geinvite voor dat team?");
                                    }
                                } else {
                                    sender.sendMessage(prefix + "Dat team bestaat niet...");
                                }
                                return true;
                            }
                        }
                        sender.sendMessage(prefix + "Usage is \n /raceteam accept [teamnaam]");
                        return true;
                    }
                } else {
                    if (args.length > 0) {
                        switch (args[0]) {
                            case "create":
                                if (args.length > 3) {
                                    Player newp = Bukkit.getPlayer(args[1]);
                                    if (newp != null) {
                                        ChatColor color = ChatColor.getByChar(args[3]);
                                        if (color != null) {
                                            TeamObject obj1 = new TeamObject(newp.getUniqueId(), args[2], color);
                                            addTeam(obj1);
                                        } else {
                                            sender.sendMessage(prefix + args[3] + " is geen valid color code!");
                                        }
                                    } else {
                                        sender.sendMessage(prefix + args[1] + " is geen geldige speler!");
                                    }
                                } else {
                                    sender.sendMessage(prefix + "Usage /raceteam create [owner] [naam] [colorcode]");
                                }
                                return true;
                            case "delete":
                                if (sender.hasPermission("zentic.admin")) {
                                    if (args.length > 1) {
                                        TeamObject team = teamObj.get(args[1].toLowerCase());
                                        if (team != null) {
                                            removeTeam(team);
                                        } else {
                                            sender.sendMessage(prefix + "Dat team bestaat niet.");
                                        }
                                    }
                                } else {
                                    sender.sendMessage(prefix + "Geen permissie.");
                                }
                                return true;
                            case "set":
                                if (args.length > 2) {
                                    TeamObject team = teamObj.get(args[1].toLowerCase());
                                    if (team != null) {
                                        switch (args[2]) {
                                            case "name":
                                                if (args.length > 3) {
                                                    team.setTeamName(args[3]);
                                                    sender.sendMessage(prefix + "Naam is aangepast.");
                                                } else {
                                                    sender.sendMessage(prefix + "Usage /raceteam set [team] [name] [name]");
                                                }
                                                return true;
                                            case "owner":
                                                if (args.length > 3) {
                                                    Player owner = Bukkit.getPlayer(args[3]);
                                                    if (owner != null) {
                                                        team.setOwnerUUID(owner.getUniqueId());
                                                        sender.sendMessage(prefix + "Owner is aangepast.");
                                                    } else {
                                                        sender.sendMessage(prefix + args[3] + " is geen geldige speler!");
                                                    }
                                                } else {
                                                    sender.sendMessage(prefix + "Usage /raceteam set [team] [owner] [owner]");
                                                }
                                                return true;
                                            case "color":
                                                if (args.length > 3) {
                                                    ChatColor color = ChatColor.getByChar(args[3]);
                                                    if (color != null) {
                                                        team.setColor(color);
                                                        sender.sendMessage(prefix + "Kleur is aangepast.");
                                                    }
                                                } else {
                                                    sender.sendMessage(prefix + "Usage /raceteam set [team] [color] [colorcode]");
                                                }
                                                return true;
                                        }
                                    } else {
                                        sender.sendMessage(prefix + args[1] + " bestaat niet?");
                                        return true;
                                    }
                                }
                            case "info":
                                if (args.length > 1) {
                                    TeamObject team = teamObj.get(args[1].toLowerCase());

                                    if (team != null) {
                                        sender.sendMessage(prefix + "Team Info \n " + team.getTeamName() + " \n" + "Owner : " + team.getOwner().getName() + "\n" + "Membercount : " + team.getTeamMembers().size() + "\n");
                                    } else {
                                        sender.sendMessage(prefix + "Dat team bestaat niet.");
                                    }
                                } else {
                                    sender.sendMessage(prefix + "Usage /raceteam info [name]");
                                }
                                return true;
                            case "member":
                                if (args.length > 3) {

                                    TeamObject team = teamObj.get(args[1].toLowerCase());
                                    if (team != null) {
                                        switch (args[1]) {
                                            case "remove":
                                                Player p = Bukkit.getPlayer(args[3]);

                                                if (p != null) {
                                                    team.removeMember(sender, p.getUniqueId());
                                                } else {
                                                    sender.sendMessage(prefix + "Die speler bestaat niet.");
                                                }
                                                return true;
                                            case "add":
                                                Player p2 = Bukkit.getPlayer(args[3]);

                                                if (p2 != null) {
                                                    TeamObject obj1 = checkTeamForPlayer(p2);
                                                    if (obj1 != null) {
                                                        obj1.removeMember(null, p2.getUniqueId());
                                                    }
                                                    team.addMember(sender, p2.getUniqueId());
                                                } else {
                                                    sender.sendMessage(prefix + "Die speler bestaat niet.");
                                                }
                                                return true;
                                        }
                                    } else {
                                        sender.sendMessage(prefix + "Dat team bestaat niet!");
                                    }
                                }
                        }
                    } else {
                        sender.sendMessage(prefix + "Usage \n /raceteam create [owner] [naam] [colorcode] \n /raceteam delete [naam] \n /raceteam set [team] [name/owner/color] [name/owner/colorcode] \n /raceteam member [team] [add/remove] [naam] \n /raceteam info [team]");
                        return true;
                    }
                }
            }
            return true;
        }

        return false;
    }


    private static void addTeam(TeamObject obj) {
        teamObj.put(obj.getTeamName(), obj);
    }

    private static void removeTeam(TeamObject obj) {
        obj.delete();
        teamObj.remove(obj.getTeamName());
    }

    public static void saveTeams() {
        File racesLoc = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/teams" + ".json").toFile();
        File path = Paths.get(ZenticRacing.getRacing().getDataFolder().toString()).toFile();
        JsonObject main = new JsonObject();
        JsonArray teamArray = new JsonArray();
        for(TeamObject teamObj : teamObj.values()) {
            JsonObject team = new JsonObject();
            team.addProperty("Name", teamObj.getTeamName());
            team.addProperty("OwnerUUID", teamObj.getOwnerUUID().toString());
            team.addProperty("ChatColor", teamObj.getColor().getChar());

            JsonArray array = new JsonArray();

            teamObj.getTeamMembers().forEach(uuid -> {
                JsonObject obj = new JsonObject();
                obj.addProperty("uuid", uuid.toString());
                array.add(obj);
            });

            team.add("Members", array);

            JsonArray array2 = new JsonArray();

            teamObj.getInvited().forEach(uuid -> {
                JsonObject obj = new JsonObject();
                obj.addProperty("uuid", uuid.toString());
                array2.add(obj);
            });

            team.add("Invited", array2);

            teamArray.add(team);
        }
        main.add("Teams", teamArray);

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

    public static void loadTeams() {
        File teamLoc = Paths.get(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/teams" + ".json").toFile();
        if(teamLoc.exists()) {
            JsonObject jsonObject = null;
            try {
                jsonObject = (JsonObject) readJson(ZenticRacing.getRacing().getDataFolder().toString() + "/storage/teams" + ".json");
            } catch (Exception ignored) {
            }

            if(jsonObject != null) {
                JsonArray array = (JsonArray) jsonObject.get("Teams");
                if(array != null) {
                    array.forEach(team -> parseTeam((JsonObject) team));
                }
            }
        }
    }

    public static void parseTeam(JsonObject team) {
        String teamName = team.get("Name").getAsString();
        String ownerUUID = team.get("OwnerUUID").getAsString();

        char chatColor = team.get("ChatColor").getAsCharacter();

        ArrayList<UUID> members = new ArrayList<>();
        JsonArray array = team.get("Members").getAsJsonArray();
        array.forEach(jsonElement -> {
            JsonObject object = jsonElement.getAsJsonObject();
            members.add(UUID.fromString(object.get("uuid").getAsString()));
        });

        ArrayList<UUID> invited = new ArrayList<>();
        JsonArray array2 = team.get("Invited").getAsJsonArray();
        array2.forEach(jsonElement -> {
            JsonObject object = jsonElement.getAsJsonObject();
            invited.add(UUID.fromString(object.get("uuid").getAsString()));
        });

        UUID owner = UUID.fromString(ownerUUID);

        TeamObject obj = new TeamObject(owner, teamName, ChatColor.getByChar(chatColor));
        obj.setTeamMembers(members);
        obj.setInvited(invited);


        teamObj.put(obj.getTeamName().toLowerCase(), obj);
    }

    public static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(reader);
    }

    public static TeamObject checkTeamForPlayer(Player player) {
        TeamObject team = null;
        for (TeamObject obj1 : teamObj.values()) {
            if (obj1.getOwnerUUID().equals(player.getUniqueId())) {
                team = obj1;
            } else {}

            if(obj1.getTeamMembers().contains(player.getUniqueId())) {
                team = obj1;
            }
        }
        return team;
    }
}
