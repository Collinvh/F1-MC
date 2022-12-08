package collinvht.projectr.commands.racing.computer;

import collinvht.projectr.commands.racing.object.ERSStorage;
import collinvht.projectr.commands.team.object.TeamObject;
import collinvht.projectr.listener.driver.object.DriverObject;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RaceCar {

    @Getter @Setter
    private SpawnedVehicle spawnedVehicle;

    @Getter
    private ERSStorage storage;

    @Getter @Setter
    private Inventory bandGui = createInventory();


    @Getter @Setter
    private DriverObject driverObject;

    @Getter
    private final TeamObject teamObject;


    public RaceCar(SpawnedVehicle spawnedVehicle, TeamObject object) {
        this.spawnedVehicle = spawnedVehicle;
        storage = new ERSStorage(this);
        teamObject = object;
    }

    public void resetStorage() {
        storage.stop();
        storage = new ERSStorage(this);
    }


    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Banden");

        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = item.getItemMeta();
        if(m != null) {
            m.setDisplayName(" ");
            item.setItemMeta(m);
            for (int i = 0; i < 27; i++) {
                inventory.setItem(i, item.clone());
            }

            inventory.setItem(13, new ItemStack(Material.AIR));
        }

        return inventory;
    }
}
