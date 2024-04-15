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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;

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

    public RaceCar(SpawnedVehicle spawnedVehicle, TeamObj linkedTeam) {
        this.linkedVehicle = spawnedVehicle;
        this.linkedTeam = linkedTeam;
        this.raceCarGUI = new RaceCarGUI(this);
        VPListener.getRACE_CARS().put(spawnedVehicle.getHolder().getUniqueId(), this);
    }

    public void updateTyre() {
        if(player != null) {
            VehicleStats stats = linkedVehicle.getStorageVehicle().getVehicleStats();
            if (baseVehicle == null) baseVehicle = linkedVehicle.getBaseVehicle();
            NBTItem tyre = raceCarGUI.getTyre();
            if (tyre == null) {
                stats.setCurrentSpeed(0.0);
                stats.setSpeed(0);
                stats.setSteering(0.0f);
            } else {
                tyre = raceCarGUI.getTyre();
                stats.setSpeed((int) (baseVehicle.getSpeedSettings().getBase() + tyre.getDouble("f1mc.extraSpeed")));
                stats.setSteering((float) (baseVehicle.getTurningRadiusSettings().getBase() + tyre.getDouble("f1mc.steering")));

                double dura = tyre.getDouble("f1mc.dura");
                double degrate = tyre.getDouble("f1mc.degradationRate");
                tyre.setDouble("f1mc.dura", (dura-degrate * (getLinkedVehicle().getCurrentSpeedInKm())/100));

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