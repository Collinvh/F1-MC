package collinvht.projectr.commands.team;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.CommandUtil;
import collinvht.projectr.commands.team.object.Bestelling;
import collinvht.projectr.commands.team.object.TeamObject;
import collinvht.projectr.util.ConfigUtil;
import com.google.gson.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
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
import java.util.Arrays;
import java.util.HashMap;
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
                if (!sender.hasPermission("projectr.team")) {
                    if (obj != null) {
                        if (args.length > 0) {
                            switch (args[0]) {
                                case "add":
                                    if (obj.getOwnerUUID().contains(((Player) sender).getUniqueId())) {
                                        if (args.length > 1) {
                                            Player member = Bukkit.getPlayer(args[1]);
                                            if (member != null) {
                                                TeamObject obj1 = checkTeamForPlayer(member);
                                                if (obj1 != null) {
                                                    if (obj1.getTeamName().equals(obj.getTeamName())) {
                                                        sender.sendMessage(serverPrefix + "Die speler zit al in jouw team!");
                                                    } else {
                                                        sender.sendMessage(serverPrefix + "Die speler zit al in een team!");
                                                    }
                                                } else {
                                                    obj.inviteMember(sender, member.getUniqueId());
                                                }
                                            } else {
                                                sender.sendMessage(serverPrefix + "Die speler bestaat niet!");
                                            }
                                        } else {
                                            sender.sendMessage(serverPrefix + "Usage /raceteam add [spelernaam]");
                                        }
                                    } else {
                                        sender.sendMessage(serverPrefix + "Jij bent geen owner!");
                                    }
                                    return true;
                                case "kick":
                                case "remove":
                                    if (obj.getOwnerUUID().contains(((Player) sender).getUniqueId())) {
                                        if (args.length > 1) {
                                            Player member = Bukkit.getPlayer(args[1]);
                                            if (member != null) {
                                                if (!member.getUniqueId().equals(((Player) sender).getUniqueId())) {
                                                    obj.removeMember(sender, member.getUniqueId());
                                                } else {
                                                    sender.sendMessage(serverPrefix + "Je bent teambaas... Je kunt niet zomaar je team verlaten.");
                                                }
                                            } else {
                                                sender.sendMessage(serverPrefix + "Die speler bestaat niet!");
                                            }
                                        } else {
                                            sender.sendMessage(serverPrefix + "Usage /raceteam remove [spelernaam]");
                                        }
                                    } else {
                                        sender.sendMessage(serverPrefix + "Jij bent geen owner!");
                                    }
                                    return true;
                                case "info":
                                    OfflinePlayer owner = obj.getOwner();
                                    sender.sendMessage(serverPrefix + "Team Info \n " + obj.getTeamName() + " \n" + "Owner : " + owner.getName() + "\n" + "Membercount : " + obj.getTeamMembers().size() + "\n");
                                    return true;
                                case "leave":
                                    if (!obj.getOwnerUUID().contains(((Player) sender).getUniqueId())) {
                                        obj.removeMember(null, ((Player) sender).getUniqueId());
                                        sender.sendMessage(serverPrefix + "Je hebt het team verlaten...");
                                    } else {
                                        sender.sendMessage(serverPrefix + "Je bent teambaas... Je kunt niet zomaar je team verlaten.");
                                    }
                            }
                        } else {
                            sender.sendMessage(serverPrefix + "Usage is \n /raceteam add [spelernaam] \n /raceteam remove [spelernaam] \n /raceteam info \n /raceteam leave");
                        }
                    } else {
                        if (args.length > 1) {
                            if (args[0].equalsIgnoreCase("accept")) {
                                TeamObject team = teamObj.get(args[1].toLowerCase());
                                if (team != null) {
                                    if (team.getInvited().contains(((Player) sender).getUniqueId())) {
                                        team.addMember(null, ((Player) sender).getUniqueId());
                                        sender.sendMessage(serverPrefix + "Team gejoined!");
                                    } else {
                                        sender.sendMessage(serverPrefix + "Je bent niet geinvite voor dat team?");
                                    }
                                } else {
                                    sender.sendMessage(serverPrefix + "Dat team bestaat niet...");
                                }
                                return true;
                            }
                        }
                        sender.sendMessage(serverPrefix + "Usage is \n /raceteam accept [teamnaam]");
                        return true;
                    }
                } else {
                    if (args.length > 0) {
                        switch (args[0].toLowerCase()) {
                            case "bestelling":
                                if(args.length > 3) {
                                    switch (args[1].toLowerCase()) {
                                        case "get":
                                            TeamObject teamObject = teamObj.get(args[2]);
                                            if(teamObject != null) {
                                                if(teamObject.getBestelling() != null) {
                                                    sender.sendMessage(serverPrefix + "Bestelling || " + teamObject.getTeamName());
                                                    sender.sendMessage(teamObject.getBestelling().getString());
                                                } else {
                                                    sender.sendMessage(serverPrefix + "Team heeft niks besteld");
                                                }
                                            } else {
                                                sender.sendMessage(serverPrefix + "Team bestaat niet.");
                                            }
                                            return false;
                                        case "reset":
                                            teamObj.forEach((s, teamObject1) -> teamObject1.setBestelling(null));
                                            sender.sendMessage(serverPrefix + "Bestellingen gereset!");
                                            return false;
                                        case "disable":
                                            ConfigUtil.setBestellenEnabled(false);
                                            sender.sendMessage("Bestellen gedisabled!");
                                            return false;
                                        case "enable":
                                            ConfigUtil.setBestellenEnabled(true);
                                            sender.sendMessage("Bestellen geenabled.");
                                            return false;
                                    }
                                }
                            case "create":
                                if (args.length > 3) {
                                    Player newp = Bukkit.getPlayer(args[1]);
                                    if (newp != null) {
                                        ChatColor color = ChatColor.getByChar(args[3]);
                                        if (color != null) {
                                            ArrayList<UUID> o = new ArrayList<>();
                                            o.add(newp.getUniqueId());
                                            TeamObject obj1 = new TeamObject(o, args[2].toLowerCase(), color);
                                            addTeam(obj1);
                                        } else {
                                            sender.sendMessage(serverPrefix + args[3] + " is geen valid color code!");
                                        }
                                    } else {
                                        sender.sendMessage(serverPrefix + args[1] + " is geen geldige speler!");
                                    }
                                } else {
                                    sender.sendMessage(serverPrefix + "Usage /raceteam create [owner] [naam] [colorcode]");
                                }
                                return true;
                            case "delete":
                                if (sender.hasPermission("projectr.admin")) {
                                    if (args.length > 1) {
                                        TeamObject team = teamObj.get(args[1].toLowerCase());
                                        if (team != null) {
                                            removeTeam(team);
                                        } else {
                                            sender.sendMessage(serverPrefix + "Dat team bestaat niet.");
                                        }
                                    }
                                } else {
                                    sender.sendMessage(serverPrefix + "Geen permissie.");
                                }
                                return true;
                            case "set":
                                if (args.length > 2) {
                                    TeamObject team = teamObj.get(args[1].toLowerCase());
                                    if (team != null) {
                                        switch (args[2].toLowerCase()) {
                                            case "name":
                                                if (args.length > 3) {
                                                    team.setTeamName(args[3]);
                                                    sender.sendMessage(serverPrefix + "Naam is aangepast.");
                                                } else {
                                                    sender.sendMessage(serverPrefix + "Usage /raceteam set [team] [name] [name]");
                                                }
                                                return true;
                                            case "owner":
                                                if (args.length > 3) {
                                                    Player owner = Bukkit.getPlayer(args[3]);
                                                    if (owner != null) {
                                                        if (team.getOwnerUUID().contains(owner.getUniqueId())) {
                                                            team.getOwnerUUID().remove(owner.getUniqueId());
                                                            sender.sendMessage(serverPrefix + "Owner is verwijderd.");
                                                        } else {
                                                            team.getOwnerUUID().add(owner.getUniqueId());
                                                            sender.sendMessage(serverPrefix + "Owner is toegevoegd.");
                                                        }
                                                    } else {
                                                        sender.sendMessage(serverPrefix + args[3] + " is geen geldige speler!");
                                                    }
                                                } else {
                                                    sender.sendMessage(serverPrefix + "Usage /raceteam set [team] [owner] [owner]");
                                                }
                                                return true;
                                            case "color":
                                                if (args.length > 3) {
                                                    ChatColor color = ChatColor.getByChar(args[3]);
                                                    if (color != null) {
                                                        team.setColor(color);
                                                        sender.sendMessage(serverPrefix + "Kleur is aangepast.");
                                                    }
                                                } else {
                                                    sender.sendMessage(serverPrefix + "Usage /raceteam set [team] [color] [colorcode]");
                                                }
                                                return true;
                                        }
                                    } else {
                                        sender.sendMessage(serverPrefix + args[1] + " bestaat niet?");
                                        return true;
                                    }
                                }
                            case "info":
                                if (args.length > 1) {
                                    TeamObject team = teamObj.get(args[1].toLowerCase());

                                    if (team != null) {
                                        sender.sendMessage(serverPrefix + "Team Info \n " + team.getTeamName() + " \n" + "Owner : " + team.getOwner().getName() + "\n" + "Membercount : " + team.getTeamMembers().size() + "\n");
                                    } else {
                                        sender.sendMessage(serverPrefix + "Dat team bestaat niet.");
                                    }
                                } else {
                                    sender.sendMessage(serverPrefix + "Usage /raceteam info [name]");
                                }
                                return true;
                            case "member":
                                if (args.length > 3) {

                                    TeamObject team = teamObj.get(args[1].toLowerCase());
                                    if (team != null) {
                                        switch (args[2]) {
                                            case "remove":
                                                Player p = Bukkit.getPlayer(args[3]);

                                                if (p != null) {
                                                    team.removeMember(sender, p.getUniqueId());
                                                } else {
                                                    sender.sendMessage(serverPrefix + "Die speler bestaat niet.");
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
                                                    sender.sendMessage(serverPrefix + "Die speler bestaat niet.");
                                                }
                                                return true;
                                        }
                                    } else {
                                        sender.sendMessage(serverPrefix + "Dat team bestaat niet!");
                                    }
                                }
                        }
                    } else {
                        sender.sendMessage(serverPrefix + "Usage \n /raceteam create [owner] [naam] [colorcode] \n /raceteam delete [naam] \n /raceteam set [team] [name/owner/color] [name/owner/colorcode] \n /raceteam member [team] [add/remove] [naam] \n /raceteam info [team]");
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
        for(TeamObject teamObj : teamObj.values()) {
            File racesLoc = Paths.get(ProjectR.getRacing().getDataFolder() + "/storage/teams/" + teamObj.getTeamName().toLowerCase() + ".json").toFile();
            File path = Paths.get(ProjectR.getRacing().getDataFolder().toString()).toFile();
            JsonObject main = new JsonObject();
            JsonObject team = new JsonObject();
            team.addProperty("Name", teamObj.getTeamName());
            team.addProperty("OwnerUUID", teamObj.getOwnerUUID().toString());
            team.addProperty("ChatColor", teamObj.getColor().getChar());

            JsonArray array = new JsonArray();

            if(teamObj.getBestelling() != null) {
                JsonObject bestelling = Bestelling.getArray(teamObj.getBestelling());
                team.add("Bestelling", bestelling);
            }

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
            main.add("TeamInfo", team);

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
    }

    public static void loadTeams() {
        File teamLoc = Paths.get(ProjectR.getRacing().getDataFolder() + "/storage/teams/").toFile();
        if(teamLoc.exists()) {
            JsonObject jsonObject;
            File[] teams = teamLoc.listFiles();
            if(teams != null) {
                for(File team : teams) {
                    try {
                        jsonObject = (JsonObject) readJson(team.getAbsolutePath() + ".json");
                        JsonObject array = jsonObject.getAsJsonObject("TeamInfo");
                        parseTeam(array);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public static void parseTeam(JsonObject team) {
        String teamName = team.get("Name").getAsString();
        String ownerUUID = team.get("OwnerUUID").getAsString();

        ownerUUID = ownerUUID.replace("[", "");
        ownerUUID = ownerUUID.replace("]", "");

        ArrayList<String> strings = new ArrayList<>(Arrays.asList(ownerUUID.split(",")));
        ArrayList<UUID> owners = new ArrayList<>();
        for (String string : strings) {
            owners.add(UUID.fromString(string));
        }

        char chatColor = team.get("ChatColor").getAsCharacter();

        Bestelling bestelling = null;
        try {
            JsonObject object = team.get("Bestelling").getAsJsonObject();
            if(object != null) {
                bestelling = Bestelling.readBestelling(object);
            }
        } catch (Exception ignored) {}

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

        TeamObject obj = new TeamObject(owners, teamName, ChatColor.getByChar(chatColor));
        obj.setTeamMembers(members);
        obj.setInvited(invited);
        obj.setBestelling(bestelling);


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
            if (obj1.getOwnerUUID().contains(player.getUniqueId())) {
                team = obj1;
            } else {}

            if(obj1.getTeamMembers().contains(player.getUniqueId())) {
                team = obj1;
            }
        }
        return team;
    }
}
