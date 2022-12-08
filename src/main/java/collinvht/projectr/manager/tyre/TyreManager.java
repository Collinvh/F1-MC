package collinvht.projectr.manager.tyre;

import collinvht.projectr.ProjectR;
import collinvht.projectr.commands.racing.computer.RaceCar;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TyreManager {
    @Getter
    private static final HashMap<String, Integer> timers = new HashMap<>();

    /*
    Starts the timer
     */
    public static void startTimer(RaceCar car) {
        if(timers.containsKey(car.getSpawnedVehicle().getStorageVehicle().getUuid())) {
            Bukkit.getScheduler().cancelTask(timers.get(car.getSpawnedVehicle().getStorageVehicle().getUuid()));
        }
        timers.put(car.getSpawnedVehicle().getStorageVehicle().getUuid(), new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack stack = car.getBandGui().getItem(13);
                if(stack != null) {
                    if(car.getSpawnedVehicle() != null) {
                        TyreManager.updateTyre(stack, car.getSpawnedVehicle(), car.getBandGui(), 13);
                    } else {
                        Bukkit.getScheduler().cancelTask(this.getTaskId());
                    }
                }
            }
        }.runTaskTimer(ProjectR.getRacing(), 0, 20).getTaskId());
    }

    public static void stopTimer(SpawnedVehicle vehicle) {
        if(vehicle != null) {
            try {
                int id = timers.get(vehicle.getStorageVehicle().getUuid());
                Bukkit.getScheduler().cancelTask(id);
                timers.remove(vehicle.getStorageVehicle().getUuid());
            } catch (Exception ignored) {
            }
        }
    }

    public static ItemStack getTyre(Tyres tyre) {
        NBTItem t = new NBTItem(new ItemStack(tyre.getMaterial()));
        t.setInteger("tyreID", tyre.getTyreID());
        t.setDouble("durability", (double) tyre.getData().getDura());


        ItemStack tyreItem = t.getItem();

        ItemMeta meta = tyreItem.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.RESET + tyre.getName());
            List<String> list = new ArrayList<>();
            list.add(ChatColor.YELLOW + "Extra snelheid : " + tyre.getData().getExtraspeed() + "km/h");
            list.add(ChatColor.GREEN + "Durability Percentage = 100%");

            meta.setLore(list);

            tyreItem.setItemMeta(meta);
        }

        return tyreItem;
    }

    public static TyreData getDataFromTyre(ItemStack stack) {
        if(stack.getType() != Material.AIR) {
            NBTItem item = new NBTItem(stack);

            if (item.getInteger("tyreID") != 0) {
                Tyres tyre = Tyres.getTyreFromID(item.getInteger("tyreID"));
                double dura = item.getDouble("durability");
                return new TyreData(tyre, dura);

            }
        }
        return new TyreData(Tyres.NULLTYRE, -1);
    }

    public static TyreData getDataFromPlayer(Player player) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(stack.getType() != Material.AIR) {
            return getDataFromTyre(stack);
        } else {
            return new TyreData(Tyres.NULLTYRE, -1);
        }
    }

    public static void updateTyre(ItemStack stack, SpawnedVehicle vehicle, Inventory inventory, int slot) {
        NBTItem item = new NBTItem(stack);

        if(item.getInteger("tyreID") != 0) {
            Tyres tyre = Tyres.getTyreFromID(item.getInteger("tyreID"));
            double dura = item.getDouble("durability");

            if(tyre != null) {
                if(vehicle != null) {
                    if (vehicle.getCurrentSpeedInKm() > 1) {
                        dura -= tyre.getData().getDegradingrate() * (((double) vehicle.getCurrentSpeedInKm()) / 100);
                        item.setDouble("durability", dura);

                        int percentage = (int) ((dura / tyre.getData().getDura()) * 100);

                        ItemStack stack1 = item.getItem();
                        ItemMeta meta = stack1.getItemMeta();

                        List<String> list = new ArrayList<>();
                        list.add(ChatColor.YELLOW + "Extra snelheid : " + tyre.getData().getExtraspeed() + "km/h");
                        list.add(ChatColor.GREEN + "Durability Percentage = " + percentage + "%");

                        meta.setLore(list);

                        stack1.setItemMeta(meta);

                        inventory.setItem(slot, stack1);
                    }
                }
            }
        }
    }

    public static void updatePlayerTyre(Player player, SpawnedVehicle vehicle) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(stack.getType() != Material.AIR) {
            updateTyre(stack, vehicle, player.getInventory(), player.getInventory().getHeldItemSlot());
        }
    }
}
