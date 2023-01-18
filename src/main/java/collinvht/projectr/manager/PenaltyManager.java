package collinvht.projectr.manager;

import collinvht.projectr.util.objects.race.ReasonableObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PenaltyManager {
    private static final HashMap<UUID, ArrayList<ReasonableObject>> PENALTIES = new HashMap<>();
    private static final HashMap<UUID, ArrayList<ReasonableObject>> WARNINGS = new HashMap<>();


    public static boolean addPenalty(UUID uniqueId, String reason) {
        PENALTIES.putIfAbsent(uniqueId, new ArrayList<>());
        ArrayList<ReasonableObject> penalties = PENALTIES.get(uniqueId);
        penalties.add(new ReasonableObject(reason, uniqueId));
        return true;
    }

    public static int countPenalties(UUID uuid) {
        if(PENALTIES.containsKey(uuid)) return PENALTIES.get(uuid).size();
        else return 0;
    }

    public static boolean addWarning(UUID uniqueId, String reason) {
        WARNINGS.putIfAbsent(uniqueId, new ArrayList<>());
        ArrayList<ReasonableObject> warnings = WARNINGS.get(uniqueId);
        warnings.add(new ReasonableObject(reason, uniqueId));
        if(warnings.size() > 4) {
            warnings.clear();
            addPenalty(uniqueId, reason);
            return false;
        }
        return true;
    }

    public static int countWarnings(UUID uuid) {
        if(WARNINGS.containsKey(uuid)) return WARNINGS.get(uuid).size();
        else return 0;
    }
}
