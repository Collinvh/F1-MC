package collinvht.projectr.util;

import collinvht.projectr.util.objects.race.RaceDriver;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    public static Location blockVectorToLocation(org.bukkit.World world, BlockVector3 vector3) {
        return new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }
    public static String millisToTimeString(final long mSec) {
        final String pattern = "mm:ss.SSS";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(mSec));
    }
    public static LinkedHashMap<RaceDriver, Long> sortByValueDesc(Map<RaceDriver, Long> map) {
        List<Map.Entry<RaceDriver, Long>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Collections.reverse(list);

        LinkedHashMap<RaceDriver, Long> result = new LinkedHashMap<>();
        for (Map.Entry<RaceDriver, Long> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
