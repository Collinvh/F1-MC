package collinvht.zenticracing.commands.racing.setup;

import lombok.Getter;

import java.util.UUID;

public class SetupOBJ {

    @Getter
    private final UUID linkedPlayer;

    private final LimitedInteger frontWingAngle = new LimitedInteger(1, 11);
    private final LimitedInteger rearWingAngle = new LimitedInteger(1, 11);

    private final LimitedFloat frontCamber = new LimitedFloat(-3.5F, -2.5F);
    private final LimitedFloat rearCamber = new LimitedFloat(-2F, -1F);

    private final LimitedFloat frontToe = new LimitedFloat(0.05F,  0.15F);
    private final LimitedFloat rearToe = new LimitedFloat(0.2F, 0.50F);

    private final LimitedInteger frontRideHeight = new LimitedInteger(1, 11);
    private final LimitedInteger rearRideHeight = new LimitedInteger(1, 11);

    private final LimitedInteger brakePressure = new LimitedInteger(50, 100);
    private final LimitedInteger brakeBias = new LimitedInteger(50, 70);

    public SetupOBJ(UUID uuid) {
        linkedPlayer = uuid;
    }

}
