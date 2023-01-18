package collinvht.projectr.manager;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.objects.TimeTrialSession;
import collinvht.projectr.util.objects.race.Race;
import nl.mtvehicles.core.events.VehicleLeaveEvent;
import nl.mtvehicles.core.infrastructure.helpers.VehicleData;
import nl.mtvehicles.core.infrastructure.models.VehicleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class TimeTrialHandler implements Listener {
    private static final HashMap<UUID, TimeTrialSession> SESSIONS = new HashMap<>();


    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new TimeTrialHandler(), ProjectR.getInstance());
    }

    public static String startSesion(Player player, Race race) {
        if(race.isTimeTrialStatus()) return "Je kunt momenteel geen time trial rijden op dit circuit";
        ItemStack stack = VehicleUtils.getItemByUUID(player, "F1BASE");
        String vehicleStr = VehicleUtils.getLicensePlate(stack);
        Location location = race.getStorage().getTimeTrialSpawn();
        if(location != null) {
            VehicleUtils.spawnVehicle(vehicleStr, location);
            SESSIONS.put(player.getUniqueId(), new TimeTrialSession(player.getUniqueId(), vehicleStr, race, player.getLocation()));
            player.teleport(location);
            new BukkitRunnable() {
                @Override
                public void run() {
                    VehicleUtils.enterVehicleWithAPI(vehicleStr, player);
                    ArmorStand mainStand = VehicleData.autostand.get("MTVEHICLES_SKIN_" + vehicleStr);
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if(!onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                            onlinePlayer.hidePlayer(ProjectR.getInstance(), player);
                            onlinePlayer.hideEntity(ProjectR.getInstance(), mainStand);
                        }
                    }
                }
            }.runTaskLater(ProjectR.getInstance(), 1);
            return "Sessie gestart, stap uit om te stoppen";
        } else {
            return "Er is geen beschikbare spawn voor je gevonden.";
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SESSIONS.forEach((s, timeTrialSession) -> {
            Player hidePlayer = Bukkit.getPlayer(timeTrialSession.getTimeTrialPlayer());
            ArmorStand mainStand = VehicleData.autostand.get("MTVEHICLES_SKIN_" + timeTrialSession.getVehiclePlate());
            if(hidePlayer != null) {
                player.hidePlayer(ProjectR.getInstance(), hidePlayer);
                player.hideEntity(ProjectR.getInstance(), mainStand);
            }
        });
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onVehicleExit(VehicleLeaveEvent event) {
        if(event.getPlayer() != null) {
            if (SESSIONS.containsKey(event.getPlayer().getUniqueId())) {
                String plate = event.getVehicle().getLicensePlate();
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        VehicleUtils.deleteVehicle(plate, event.getPlayer(), false);
                        event.getPlayer().teleport(SESSIONS.get(event.getPlayer().getUniqueId()).getTpLocation());
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.showPlayer(ProjectR.getInstance(), event.getPlayer());
                        }
                        SESSIONS.get(event.getPlayer().getUniqueId()).quit();
                        SESSIONS.remove(event.getPlayer().getUniqueId());
                    }
                };
                runnable.runTaskLater(ProjectR.getInstance(), 5);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerExit(PlayerQuitEvent event) {
        if(SESSIONS.get(event.getPlayer().getUniqueId()) != null) {
            SESSIONS.get(event.getPlayer().getUniqueId()).quit();
            SESSIONS.remove(event.getPlayer().getUniqueId());
        }
    }
}
