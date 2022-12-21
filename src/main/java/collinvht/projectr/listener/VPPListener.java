//package collinvht.projectr.listener;
//
//import collinvht.projectr.ProjectR;
//import collinvht.projectr.util.objects.race.RaceDriver;
//import lombok.Getter;
//import me.legofreak107.vehiclesplus.VehiclesPlus;
//import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
//import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleEnterEvent;
//import me.legofreak107.vehiclesplus.vehicles.api.events.VehicleLeaveEvent;
//import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
//import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
//import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.Part;
//import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.Seat;
//import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.skins.Colorable;
//import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.skins.Skin;
//import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.base.storage.StorageHitbox;
//import org.bukkit.Bukkit;
//import org.bukkit.Color;
//import org.bukkit.Material;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerQuitEvent;
//import org.bukkit.inventory.ItemFactory;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.Damageable;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.UUID;
//
//public class VPPListener implements Listener {
//    @Getter
//    private static final HashMap<UUID, RaceDriver> raceDrivers = new HashMap<>();
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public static void OnVehicleEnter(VehicleEnterEvent event) {
//        SpawnedVehicle vehicle = (SpawnedVehicle) event.getVehicle();
//        RaceDriver driver = raceDrivers.get(event.getDriver().getUniqueId());
//        if(driver == null) {
//            driver = new RaceDriver();
//            raceDrivers.put(event.getDriver().getUniqueId(), driver);
//        }
//
//        driver.setVehicle(vehicle);
//        driver.setDriving(true);
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public static void OnVehicleLeave(VehicleLeaveEvent event) {
//        if(containsDriver(event.getDriver().getUniqueId())) {
//            RaceDriver driver = raceDrivers.get(event.getDriver().getUniqueId());
//            driver.setDriving(false);
//            driver.setVehicle(null);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public static void OnPlayerLeave(PlayerQuitEvent event) {
//        if(containsDriver(event.getPlayer().getUniqueId())) {
//            RaceDriver driver = raceDrivers.get(event.getPlayer().getUniqueId());
//            driver.setDriving(false);
//            driver.setVehicle(null);
//        }
//    }
//
//
//    private static boolean containsDriver(UUID uuid) {
//        return raceDrivers.containsKey(uuid);
//    }
//
//    public static void initialize() {
//        Bukkit.getPluginManager().registerEvents(new VPPListener(), ProjectR.getInstance());
//    }
//}
