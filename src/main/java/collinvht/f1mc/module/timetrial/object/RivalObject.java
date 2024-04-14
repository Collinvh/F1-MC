package collinvht.f1mc.module.timetrial.object;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RivalObject {
    private final UUID uuid;
    private final UUID rival;
    private TimeTrialLap lap;

    public RivalObject(UUID uuid, UUID rival) {
        this.uuid = uuid;
        this.rival = rival;
    }

    public void setLap(String name) {
        this.lap = TimeTrialLap.fromUUID(rival, name);
    }
}
