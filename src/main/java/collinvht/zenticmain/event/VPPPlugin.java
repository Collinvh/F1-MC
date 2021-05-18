package collinvht.zenticmain.event;

import lombok.Getter;
import me.legofreak107.vehiclesplus.vehicles.api.events.*;

public abstract class VPPPlugin {

    @Getter
    private final String plName;
    public VPPPlugin(String PLName) {
        this.plName = PLName;
    }

    public abstract void VehicleEnterEvent(VehicleEnterEvent event);
    public abstract void VehicleLeaveEvent(VehicleLeaveEvent event);
    public abstract void VehicleCollisionEvent(VehicleCollisionEvent event);
    public abstract void VehicleSpawnedEvent(VehicleSpawnedEvent event);
    public abstract void VehicleDestroyEvent(VehicleDestroyEvent event);
    public abstract void VehicleDamageEvent(VehicleDamageEvent event);
    public abstract void VehicleBuyEvent(VehicleBuyEvent event);
}
