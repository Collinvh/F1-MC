package collinvht.projectr.util;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Location blockVectorToLocation(org.bukkit.World world, BlockVector3 vector3) {
        return new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static String millisToTimeString(final long mSec) {
        final String pattern = "mm:ss.SSS";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(mSec));
    }
}
