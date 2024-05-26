package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.module.racing.module.team.object.TeamObj;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.Utils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.VehicleStats;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RaceCar {
    @Getter
    private final SpawnedVehicle linkedVehicle;
    @Getter
    private final TeamObj linkedTeam;
    @Getter @Setter
    private RaceDriver player;
    @Getter
    private final RaceCarGUI raceCarGUI;
    private BaseVehicle baseVehicle;
    @Getter
    private static final Timer carTimers = new Timer("RaceCar Timers");

    public RaceCar(SpawnedVehicle spawnedVehicle, TeamObj linkedTeam) {
        this.linkedVehicle = spawnedVehicle;
        this.linkedTeam = linkedTeam;
        this.raceCarGUI = new RaceCarGUI(this);
        VPListener.getRACE_CARS().put(spawnedVehicle.getHolder().getUniqueId(), this);
        carTimers.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTyre();
            }
        }, 0, 1);
    }

    public void updateTyre() {
        if(player != null) {
            VehicleStats stats = linkedVehicle.getStorageVehicle().getVehicleStats();
            if (baseVehicle == null) baseVehicle = linkedVehicle.getBaseVehicle();
            NBTItem tyre = raceCarGUI.getTyre();
            if (tyre == null) {
                stats.setCurrentSpeed(0.0);
                stats.setSpeed(0);
                stats.setHighSpeedAcceleration(0.0f);
                stats.setLowSpeedAcceleration(0.0f);
                stats.setLowSpeedSteering(0.0f);
                stats.setHighSpeedSteering(0.0f);
            } else {
                double dura = tyre.getDouble("f1mc.dura");
                double degrate = tyre.getDouble("f1mc.degradationRate")/20;
                if(dura <= 0) {
                    if(stats.getCurrentSpeed() > 10) stats.setCurrentSpeed(10.0);
                    stats.setSpeed(10);
                    return;
                }
                if(!player.isInPit()) {
                    stats.setSpeed((int) (baseVehicle.getSpeedSettings().getBase() + tyre.getDouble("f1mc.extraSpeed")));
                } else {
                    if(stats.getCurrentSpeed() > 60.5D) {
                        stats.setCurrentSpeed(60.00D);
                    }
                    stats.setSpeed(60);
                }
                stats.setLowSpeedSteering((float) (baseVehicle.getTurningRadiusSettings().getLowSpeed() * tyre.getDouble("f1mc.steering")));
                stats.setHighSpeedSteering((float) (baseVehicle.getTurningRadiusSettings().getHighSpeed() * tyre.getDouble("f1mc.steering")));
                stats.setLowSpeedAcceleration((float) (baseVehicle.getAccelerationSettings().getLowSpeed() * tyre.getDouble("f1mc.steering")));
                stats.setHighSpeedAcceleration((float) (baseVehicle.getAccelerationSettings().getHighSpeed() * tyre.getDouble("f1mc.steering")));

                tyre.setDouble("f1mc.dura", (dura-degrate * (getLinkedVehicle().getCurrentSpeedInKm())/5000));
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Durability left = " + Utils.round(dura - degrate, 1) + "/" + tyre.getDouble("f1mc.maxdura"));
                lore.add(ChatColor.GRAY + "Extra Speed = " + tyre.getDouble("f1mc.extraSpeed") + "km/h");
                ItemStack stack = tyre.getItem();
                if(stack.getItemMeta() != null) {
                    ItemMeta meta = stack.getItemMeta();
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                }
                raceCarGUI.getBandInventory().forceSetItem(UpdateReason.SUPPRESSED, 0, stack);
            }
        }
    }
}