package collinvht.f1mc.module.buildingtools.manager;

import collinvht.f1mc.module.buildingtools.obj.MemorizedEdit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CustomManager {
    private static final HashMap<UUID, ArrayList<MemorizedEdit>> edits = new HashMap<>();

    public static void addEdit(UUID uuid, MemorizedEdit edit) {
        if(!edits.containsKey(uuid)) {
            edits.put(uuid, new ArrayList<>());
        }
        ArrayList<MemorizedEdit> list = edits.get(uuid);
        if(list.size() + 1 > 3) {
            list.remove(0);
        }
        list.add(edit);
    }

    public static String undo(UUID edit) {
        if(edits.containsKey(edit)) {
            if(edits.get(edit).get(0) != null) {
                edits.get(edit).get(0).undo();
                edits.get(edit).remove(0);
                return "Edit has been undone";
            }
        }
        return "No previous edits found.";
    }
}
