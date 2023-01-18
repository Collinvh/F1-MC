package collinvht.projectr.util.objects.race;

import lombok.Getter;

import java.util.UUID;

public class ReasonableObject {
    @Getter
    private final String reason;
    @Getter
    private final UUID uuid;
    public ReasonableObject(String reason, UUID uniqueId) {
        this.reason = reason;
        this.uuid = uniqueId;
    }
}
