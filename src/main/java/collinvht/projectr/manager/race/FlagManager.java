package collinvht.projectr.manager.race;

import collinvht.projectr.util.enums.FlagType;
import collinvht.projectr.util.objects.LimitedObject;
import collinvht.projectr.util.objects.SharedObject;
import collinvht.projectr.util.objects.race.Race;
import lombok.Getter;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlagManager {
    @Getter
    private static final HashMap<UUID, SharedObject<Integer, Race>> EDITING = new HashMap<>();

    public static void editFlagLocations(Race race, int sector, UUID uuid) {
        EDITING.put(uuid, new SharedObject<>(sector, race));
    }

    public static void setAll(Race race, FlagType type) {
        race.getFlags().setS1(type);
        race.getFlags().setS2(type);
        race.getFlags().setS3(type);
    }

    public static String stopEditing(UUID uniqueId) {
        if(EDITING.containsKey(uniqueId)) {
            EDITING.remove(uniqueId);
            return "Stopped editing.";
        } else {
            return "You weren't editing.";
        }
    }

    public static void set(Race race, FlagType type, int sector) {
        switch (sector) {
            case 1:
                race.getFlags().setS1(type);
                break;
            case 2:
                race.getFlags().setS2(type);
                break;
            case 3:
                race.getFlags().setS3(type);
                break;
        }
    }
}
