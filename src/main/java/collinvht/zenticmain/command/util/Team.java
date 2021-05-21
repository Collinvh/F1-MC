package collinvht.zenticmain.command.util;

import collinvht.zenticmain.ZenticMain;
import collinvht.zenticmain.command.CommandUtil;
import collinvht.zenticmain.obj.TeamObj;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.UUID;

public class Team implements CommandExecutor, CommandUtil, Listener {

    private static final ArrayList<TeamObj> teamObj = new ArrayList<>();
    private static final String zentic = "" + ChatColor.RED + ChatColor.BOLD + "ZT > " + ChatColor.RESET;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            TeamObj obj = checkTeamForPlayer(player);
            if (!sender.hasPermission("zentic.fia")) {
                if (obj != null) {
                    if (args.length > 0) {
                        switch (args[0]) {
                            case "add":
                                if (args.length > 1) {
                                    Player member = Bukkit.getPlayer(args[1]);
                                    if (member != null) {
                                        obj.addMember(sender, member.getUniqueId());
                                    } else {
                                        sender.sendMessage(zentic + "Die speler bestaat niet!");
                                    }
                                } else {
                                    sender.sendMessage(zentic + "Usage /team add [spelernaam]");
                                }
                                return true;
                            case "kick":
                            case "remove":
                                if (args.length > 1) {
                                    Player member = Bukkit.getPlayer(args[1]);
                                    if (member != null) {
                                        obj.removeMember(sender, member.getUniqueId());
                                    } else {
                                        sender.sendMessage(zentic + "Die speler bestaat niet!");
                                    }
                                } else {
                                    sender.sendMessage(zentic + "Usage /team remove [spelernaam]");
                                }
                                return true;
                            case "info":
                                Player owner = obj.getOwner();
                                sender.sendMessage(zentic + "Team Info \n " + obj.getTeamName() + " \n" + "Owner : " + owner.getName() + "\n" + "Membercount : " + obj.getTeamMembers().size() + "\n");
                                return true;
                        }
                    } else {
                        sender.sendMessage(zentic + "Usage is \n /team add [spelernaam] \n /team remove [spelernaam] \n /team info");
                    }
                } else {
                    sender.sendMessage(zentic + "Je zit niet in een team!");
                    return false;
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
                                        TeamObj obj1 = new TeamObj(newp.getUniqueId(), args[2], color);
                                        addTeam(obj1);
                                    } else {
                                        sender.sendMessage(zentic + args[3] + " is geen valid color code!");
                                    }
                                } else {
                                    sender.sendMessage(zentic + args[1] + " is geen geldige speler!");
                                }
                            } else {
                                sender.sendMessage(zentic + "Usage /team create [owner] [naam] [colorcode]");
                            }
                            return true;
                        case "delete":
                            if (sender.hasPermission("zentic.admin")) {
                                if (args.length > 1) {
                                    TeamObj team = null;
                                    for (TeamObj obj1 : teamObj) {
                                        if (obj1.getTeamName().equalsIgnoreCase(args[1])) {
                                            team = obj1;
                                        }
                                    }
                                    if (team != null) {
                                        removeTeam(team);
                                    } else {
                                        sender.sendMessage(zentic + "Dat team bestaat niet.");
                                    }
                                }
                            } else {
                                sender.sendMessage(zentic + "Geen permissie.");
                            }
                            return true;
                        case "set":
                            if (args.length > 2) {
                                TeamObj team = null;
                                for (TeamObj obj1 : teamObj) {
                                    if (obj1.getTeamName().equalsIgnoreCase(args[1])) {
                                        team = obj1;
                                    }
                                }

                                if (team != null) {
                                    switch (args[2]) {
                                        case "name":
                                            if (args.length > 3) {
                                                team.setTeamName(args[3]);
                                                sender.sendMessage(zentic + "Naam is aangepast.");
                                            } else {
                                                sender.sendMessage(zentic + "Usage /team set [team] [name] [name]");
                                            }
                                            return true;
                                        case "owner":
                                            if (args.length > 3) {
                                                Player owner = Bukkit.getPlayer(args[3]);
                                                if (owner != null) {
                                                    team.setOwnerUUID(owner.getUniqueId());
                                                    sender.sendMessage(zentic + "Owner is aangepast.");
                                                } else {
                                                    sender.sendMessage(zentic + args[3] + " is geen geldige speler!");
                                                }
                                            } else {
                                                sender.sendMessage(zentic + "Usage /team set [team] [owner] [owner]");
                                            }
                                            return true;
                                        case "color":
                                            if (args.length > 3) {
                                                ChatColor color = ChatColor.getByChar(args[3]);
                                                if (color != null) {
                                                    team.setColor(color);
                                                    sender.sendMessage(zentic + "Kleur is aangepast.");
                                                }
                                            } else {
                                                sender.sendMessage(zentic + "Usage /team set [team] [color] [colorcode]");
                                            }
                                            return true;
                                    }
                                } else {
                                    sender.sendMessage(zentic + args[1] + " bestaat niet?");
                                    return true;
                                }
                            }
                        case "info":
                            if (args.length > 1) {
                                TeamObj team = null;
                                for (TeamObj obj1 : teamObj) {
                                    if (obj1.getTeamName().equalsIgnoreCase(args[1])) {
                                        team = obj1;
                                    }
                                }

                                if (team != null) {
                                    sender.sendMessage(zentic + "Team Info \n " + team.getTeamName() + " \n" + "Owner : " + team.getOwner().getName() + "\n" + "Membercount : " + team.getTeamMembers().size() + "\n");
                                } else {
                                    sender.sendMessage(zentic + "Dat team bestaat niet.");
                                }
                            } else {
                                sender.sendMessage(zentic + "Usage /team info [name]");
                            }
                            return true;
                        case "member":
                            if (args.length > 3) {

                                TeamObj team = null;
                                for (TeamObj obj1 : teamObj) {
                                    if (obj1.getTeamName().equalsIgnoreCase(args[1])) {
                                        team = obj1;
                                    }
                                }
                                if (team != null) {
                                    switch (args[1]) {
                                        case "remove":
                                            Player p = Bukkit.getPlayer(args[3]);

                                            if (p != null) {
                                                team.removeMember(sender, p.getUniqueId());
                                            } else {
                                                sender.sendMessage(zentic + "Die speler bestaat niet.");
                                            }
                                            return true;
                                        case "add":
                                            Player p2 = Bukkit.getPlayer(args[3]);

                                            if (p2 != null) {
                                                team.addMember(sender, p2.getUniqueId());
                                            } else {
                                                sender.sendMessage(zentic + "Die speler bestaat niet.");
                                            }
                                            return true;
                                    }
                                } else {
                                    sender.sendMessage(zentic + "Dat team bestaat niet!");
                                }
                            }
                    }
                } else {
                    sender.sendMessage(zentic + "Usage \n /team create [owner] [naam] [colorcode] \n /team delete [naam] \n /team set [team] [name/owner/color] [name/owner/colorcode] \n /team member [team] [add/remove] [naam]");
                    return true;
                }
            }

            return true;
        }

        return false;
    }


    private static void addTeam(TeamObj obj) {
        teamObj.add(obj);
    }

    private static void removeTeam(TeamObj obj) {
        obj.delete();
        teamObj.remove(obj);
    }

    public static void saveTeams() {
        File racesLoc = Paths.get(ZenticMain.getInstance().getDataFolder().toString() + "/teams" + ".json").toFile();
        File path = Paths.get(ZenticMain.getInstance().getDataFolder().toString()).toFile();
        JsonObject main = new JsonObject();
        JsonArray teamArray = new JsonArray();
        for(TeamObj teamObj : teamObj) {
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
        File teamLoc = Paths.get(ZenticMain.getInstance().getDataFolder().toString() + "/teams" + ".json").toFile();
        if(teamLoc.exists()) {
            JsonObject jsonObject = null;
            try {
                jsonObject = (JsonObject) readJson(ZenticMain.getInstance().getDataFolder().toString() + "/teams" + ".json");
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

        UUID owner = UUID.fromString(ownerUUID);

        TeamObj obj = new TeamObj(owner, teamName, ChatColor.getByChar(chatColor));
        obj.setTeamMembers(members);


        teamObj.add(obj);
    }

    public static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(reader);
    }

    private TeamObj checkTeamForPlayer(Player player) {
        for (TeamObj teamObj1 : teamObj) {
            if (teamObj1.getOwnerUUID() == player.getUniqueId()) {
                return teamObj1;
            }

            for(UUID teamMember : teamObj1.getTeamMembers()) {
                if(teamMember == player.getUniqueId()) {
                    return teamObj1;
                }
            }
        }
        return null;
    }
}
