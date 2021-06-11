package collinvht.zenticracing.util.objs;

import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import org.bukkit.entity.Player;

public class VehicleWPlayer {


    @Getter @Setter
    public SpawnedVehicle vehicle;

    @Getter @Setter
    public Player player;
}
