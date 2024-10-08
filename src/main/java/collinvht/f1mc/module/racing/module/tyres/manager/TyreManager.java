package collinvht.f1mc.module.racing.module.tyres.manager;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.tyres.listeners.TyreListeners;
import collinvht.f1mc.module.racing.module.tyres.obj.TyreBaseObject;
import collinvht.f1mc.util.Utils;
import collinvht.f1mc.util.modules.ModuleBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class TyreManager extends ModuleBase {
    @Getter
    private static final HashMap<String, TyreBaseObject> tyres = new HashMap<>();
    @Getter @Setter
    private static String track = "none";

    public static boolean isTyre(ItemStack stack) {
        if(stack == null) return false;
        if(stack.getType().isAir()) return false;
        if(stack.getAmount() <= 0) return false;
        NBTItem item = new NBTItem(stack);
        if(item.hasCustomNbtData()) {
            if(item.hasTag("f1mc.isTyre")) {
                return item.getString("f1mc.track").equalsIgnoreCase(track);
            }
        }
        Bukkit.getLogger().warning("notyre");
        return false;
    }

    public static String getTyreName(ItemStack stack) {
        if(stack == null) return "Null";
        if(stack.getType().isAir()) return "Null";
        if(stack.getAmount() <= 0) return "Null";
        NBTItem item = new NBTItem(stack);
        if(item.hasCustomNbtData()) {
            if(item.hasTag("f1mc.tyreName")) {
                return item.getString("f1mc.tyreName");
            } else {
                ItemMeta meta = stack.getItemMeta();
                if(meta != null) {
                    Bukkit.getLogger().warning(meta.getDisplayName().replace("Tyre", "").replace(" ", "").replace("§7", ""));
                    return meta.getDisplayName().replace("Tyre", "").replace(" ", "").replace("§7", "");
                }
                return "Null";
            }
        }
        return "Null";
    }

    public static ItemStack getTyre(String tyreName) {
        TyreBaseObject tyreBaseObject = tyres.get(tyreName.toLowerCase());
        ItemStack tyre = new ItemStack(Material.STICK);
        ItemMeta meta = tyre.getItemMeta();
        if(meta != null) {
            meta.setCustomModelData(tyreBaseObject.getModelData());
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Durability left = " + tyreBaseObject.getMaxDurability() + "/" + tyreBaseObject.getMaxDurability());
            lore.add(ChatColor.GRAY + "Extra Speed = " + tyreBaseObject.getExtraSpeed() + "km/h");
            meta.setLore(lore);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + tyreBaseObject.getName().substring(0, 1).toUpperCase() + tyreBaseObject.getName().substring(1) + " Tyre");
            tyre.setItemMeta(meta);
        }
        NBTItem nbtTyre = new NBTItem(tyre);
        nbtTyre.setBoolean("f1mc.isTyre", true);
        nbtTyre.setDouble("f1mc.dura", tyreBaseObject.getMaxDurability());
        nbtTyre.setString("f1mc.name", tyreBaseObject.getName());
        nbtTyre.setDouble("f1mc.maxdura", tyreBaseObject.getMaxDurability());
        nbtTyre.setDouble("f1mc.steering", tyreBaseObject.getSteering());
        nbtTyre.setDouble("f1mc.extraSpeed", tyreBaseObject.getExtraSpeed());
        nbtTyre.setDouble("f1mc.degradationRate", tyreBaseObject.getDegradingRate());
        nbtTyre.setString("f1mc.track", track);
        return nbtTyre.getItem();
    }

    @Override
    public void load() {
        File files = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/tyres.json").toFile();
        if(files.exists()) {
            try {
                JsonObject object = (JsonObject) Utils.readJson(files.getAbsolutePath());
                if(object.get("currentTrack") != null) {
                    track = object.get("currentTrack").getAsString();
                }
                JsonArray array = object.getAsJsonArray("tyres");
                for (JsonElement jsonElement : array) {
                    JsonObject object2 = jsonElement.getAsJsonObject();
                    String name = object2.get("name").getAsString();
                    tyres.put(name, new TyreBaseObject(name, object2.get("modelData").getAsInt(), object2.get("maxDura").getAsDouble(), object2.get("degradingRate").getAsDouble(), object2.get("steering").getAsDouble(), object2.get("extraSpeed").getAsDouble()));

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            tyres.put("soft", new TyreBaseObject("soft", 10012, 1500, 1.15, 1.1, 15));
            tyres.put("medium", new TyreBaseObject("medium", 10013, 1650, 1.10, 1.05, 7.5));
            tyres.put("hard", new TyreBaseObject("soft", 10014, 1800, 1.05, 1, 0));
        }
        attachModule(new TyreListeners());
    }

    @Override
    public void saveModule() {
        File path = Paths.get(F1MC.getInstance().getDataFolder() + "/storage/").toFile();
        JsonObject object = new JsonObject();
        JsonArray mainObject = new JsonArray();
        tyres.forEach((name, tyre) -> {
            JsonObject object2 = new JsonObject();
            object2.addProperty("name", name);
            object2.addProperty("modelData", tyre.getModelData());
            object2.addProperty("extraSpeed", tyre.getExtraSpeed());
            object2.addProperty("degradingRate", tyre.getDegradingRate());
            object2.addProperty("steering", tyre.getSteering());
            object2.addProperty("maxDura", tyre.getMaxDurability());
            mainObject.add(object2);
        });
        object.addProperty("currentTrack", track);
        object.add("tyres", mainObject);

        Utils.saveJSON(path, "tyres", object);
    }
}
