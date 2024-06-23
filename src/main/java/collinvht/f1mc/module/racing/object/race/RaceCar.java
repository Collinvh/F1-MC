package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.module.team.object.TeamObj;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.Utils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.VehicleStats;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.joml.Math;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
    private final ScheduledTask task;

    @Getter
    private FMMode currentMode = FMMode.MEDIUM;
    @Getter
    private ERSMode currentERSMode = ERSMode.BALANCED;

    @Getter
    private double currentERS = 200;

    public RaceCar(SpawnedVehicle spawnedVehicle, TeamObj linkedTeam) {
        this.linkedVehicle = spawnedVehicle;
        this.linkedTeam = linkedTeam;
        this.raceCarGUI = new RaceCarGUI(this);
        VPListener.getRACE_CARS().put(spawnedVehicle.getHolder().getUniqueId(), this);
        this.task = F1MC.getAsyncScheduler().runAtFixedRate(F1MC.getInstance(), new Consumer<>() {
            int curTick = 0;
            @Override
            public void accept(ScheduledTask scheduledTask) {
                updateTyre();

                if (curTick == 0) {
                    if (spawnedVehicle.getCurrentSpeedInKm() > 0) {
                        if (currentERSMode == ERSMode.OFF && currentERS == 200) {
                            return;
                        }
                        if (currentERS - currentERSMode.getUsage() < 0) {
                            currentERS = 0;
                            currentERSMode = ERSMode.OFF;
                        } else if (currentERS + currentERSMode.getRegain() > 200) {
                            currentERS = 100;
                        } else {
                            currentERS -= currentERSMode.getUsage();
                            currentERS += currentERSMode.getRegain();
                            curTick++;
                        }
                    }
                } else {
                    curTick++;
                    if (curTick >= 150) {
                        curTick = 0;
                    }
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
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
                double maxDura = tyre.getDouble("f1mc.maxdura");
                double currentWear = dura/maxDura;
                if(dura <= 0) {
                    if(stats.getCurrentSpeed() > 10.05D) {
                        stats.setCurrentSpeed(10.00);
                    }
                    stats.setSpeed(10);
                    return;
                }
//                double[] tyreSpeedArray = new double[3];
//                if(RaceManager.getDrivingPlayers().get(player.getPlayer()) != null) {
//                    tyreSpeedArray = WeatherManager.currentRotation(RaceManager.getDrivingPlayers().get(player.getPlayer()));
//                } else {
//                    tyreSpeedArray[0] = WeatherTypes.DRY.getInterSpeedMultiplier();
//                    tyreSpeedArray[1] = WeatherTypes.DRY.getWetSpeedMultiplier();
//                    tyreSpeedArray[2] = WeatherTypes.DRY.getSlickSpeedMultiplier();
//                }
//                String tyreName = tyre.getString("f1mc.name");
                float speedMultiplier = 1;
//                switch (tyreName.toLowerCase()) {
//                    case "intermediate" -> speedMultiplier = (float) tyreSpeedArray[0];
//                    case "wet" -> speedMultiplier = (float) tyreSpeedArray[1];
//                    default -> speedMultiplier = (float) tyreSpeedArray[2];
//                }
//                if(dura <= 0) {
//                    if(stats.getCurrentSpeed() > 10) stats.setCurrentSpeed(10.0);
//                    stats.setSpeed(10);
//                    return;
//                }
                if(!player.isInPit()) {
                    stats.setSpeed((int) (baseVehicle.getSpeedSettings().getBase() + currentERSMode.getExtraSpeed() + currentMode.getExtraSpeed() + Math.lerp(0, tyre.getDouble("f1mc.extraSpeed"), currentWear) /* * speedMultiplier*/));
                } else {
                    if(stats.getCurrentSpeed() > 60.5D) {
                        stats.setCurrentSpeed(60.00D);
                    }
                    stats.setSpeed(60);
                }
                stats.setLowSpeedSteering((float) ((baseVehicle.getTurningRadiusSettings().getLowSpeed() * tyre.getDouble("f1mc.steering")) * speedMultiplier));
                stats.setHighSpeedSteering((float) ((baseVehicle.getTurningRadiusSettings().getHighSpeed() * tyre.getDouble("f1mc.steering")) * speedMultiplier));
                stats.setLowSpeedAcceleration((float) ((baseVehicle.getAccelerationSettings().getLowSpeed() * tyre.getDouble("f1mc.steering")) * speedMultiplier));
                stats.setHighSpeedAcceleration((float) ((baseVehicle.getAccelerationSettings().getHighSpeed() * tyre.getDouble("f1mc.steering")) * speedMultiplier));

                tyre.setDouble("f1mc.dura", (dura-(degrate * (getLinkedVehicle().getCurrentSpeedInKm()/100) * getLinkedVehicle().getStorageVehicle().getVehicleStats().getCurrentSteer())));
                ArrayList<String> lore = new ArrayList<>();
                //Todo: fix deprecated
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

    public void delete() {
        if(linkedVehicle != null) {
            linkedVehicle.despawn(true);
        }
        this.task.cancel();
    }

    public void updateFM(FMMode fmMode) {
        if(currentMode != fmMode) {
            currentMode = fmMode;
        }
    }

    public void updateERS(ERSMode fmMode) {
        if(currentERSMode != fmMode) {
            currentERSMode = fmMode;
        }
    }
}