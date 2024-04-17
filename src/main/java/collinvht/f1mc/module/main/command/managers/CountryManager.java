package collinvht.f1mc.module.main.command.managers;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.main.gui.CountryGUIs;
import collinvht.f1mc.module.main.objects.CountryObject;
import collinvht.f1mc.util.DefaultMessages;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CountryManager extends ModuleBase {
    private static final HashMap<String, CountryObject> countries = new HashMap<>();
    @Getter
    private static final HashMap<UUID, CountryObject> playerPerCountry = new HashMap<>();
    public static List<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        if(!countries.isEmpty()) {
            countries.forEach((s, countryObject) -> items.add(new SimpleItem(countryObject.getStack(), (click) -> {
                CountryManager.updateCountry(click.getPlayer(), countryObject.getCountryName());
                click.getPlayer().closeInventory();
                click.getPlayer().sendMessage(DefaultMessages.PREFIX + "Changed your country!");
            })));
        }
        return items;
    }

    @Override
    public void load() {
        File file = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/countries.json").toFile();
        if(file.exists()) {
            try {
                JsonObject object = (JsonObject) Utils.readJson(file.getAbsolutePath());
                JsonArray array = object.getAsJsonArray("countries");
                for (JsonElement jsonElement : array) {
                    JsonObject object2 = jsonElement.getAsJsonObject();
                    String countryName = object2.get("countryName").getAsString();
                    String countryShort = object2.get("countryShort").getAsString();
                    String countryImg = object2.get("countryImg").getAsString();
                    int headID = object2.get("headID").getAsInt();
                    ArrayList<UUID> uuids = new ArrayList<>();
                    JsonArray array1 = object2.get("playerUUIDS").getAsJsonArray();
                    for (JsonElement element : array1) {
                        uuids.add(UUID.fromString(element.getAsString()));
                    }
                    CountryObject object1 = new CountryObject(countryName, countryShort, countryImg, headID);
                    object1.setPlayers(uuids);
                    countries.put(countryName, object1);
                    for (UUID uuid : uuids) {
                        playerPerCountry.put(uuid, object1);
                    }
                }
            } catch (Exception ignored) {}
        } else {
            try {
                File cfgFile = Paths.get(F1MC.getInstance().getDataFolder() + "/countries.yml").toFile();
                if(cfgFile.exists()) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(Files.newInputStream(cfgFile.toPath())));
                    HashMap<String, String> stringHashMap = new HashMap<>();

                    String line;
                    try {
                        while((line = input.readLine()) != null) {
                            String[] strings = line.split(":");
                            stringHashMap.put(strings[0].replace(" ", ""), strings[1].replace(" ", ""));
                        }
                    } finally {
                        input.close();
                    }
                    stringHashMap.forEach((s, s2) -> countries.put(s, new CountryObject(s, s, s2, 0)));
                }
            } catch (Exception ignored) {}
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updatePlayer(onlinePlayer);
        }
    }

    @Override
    public void saveModule() {
        File path = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/").toFile();
        JsonObject object = new JsonObject();
        JsonArray mainObject = new JsonArray();
        countries.forEach((str, countryObject) -> {
            JsonObject object2 = new JsonObject();
            object2.addProperty("countryName", countryObject.getCountryName());
            object2.addProperty("countryShort", countryObject.getCountryShort());
            object2.addProperty("countryImg", countryObject.getCountryImg());
            object2.addProperty("headID", countryObject.getHeadID());
            JsonArray array = new JsonArray();
            for (UUID player : countryObject.getPlayers()) {
                array.add(player.toString());
            }
            object2.add("playerUUIDS", array);
            mainObject.add(object2);
        });
        object.add("countries", mainObject);

        Utils.saveJSON(path, "countries", object);
    }

    public static void updatePlayer(Player player) {
        if(playerPerCountry.get(player.getUniqueId()) != null) {
            CountryObject countryObject = playerPerCountry.get(player.getUniqueId());
            countryObject.updateTag(player);
        } else {
            updateCountry(player, "none");
            Window.single().setTitle(org.bukkit.ChatColor.GRAY + "Select your country!").setGui(CountryGUIs.getCountryGUI()).build(player).open();
        }
    }

    public static void updateCountry(Player player, String country) {
        country = country.toLowerCase();
        if(countries.get(country) != null) {
            if(playerPerCountry.get(player.getUniqueId()) != null) {
                playerPerCountry.get(player.getUniqueId()).getPlayers().remove(player.getUniqueId());
                playerPerCountry.remove(player.getUniqueId());
            }
            countries.get(country).addPlayer(player);
        } else {
            for (CountryObject value : countries.values()) {
                if(value.getCountryShort().equals(country)) {
                    if(playerPerCountry.get(player.getUniqueId()) != null) {
                        playerPerCountry.get(player.getUniqueId()).getPlayers().remove(player.getUniqueId());
                        playerPerCountry.remove(player.getUniqueId());
                    }
                    value.addPlayer(player);
                    return;
                }
            }
        }
    }
}