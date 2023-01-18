package collinvht.projectr.commands.fun;

import collinvht.projectr.ProjectR;
import collinvht.projectr.util.objects.commands.CommandUtil;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplLoader;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Hypersoft extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/hypersoft [player]", ((sender, command, label, args) -> {
            Player player = Bukkit.getPlayer(args[0]);
            if(player != null) {
                new BukkitRunnable() {
                    int i = 0;
                    @Override
                    public void run() {
                        if(i<250){
                            player.teleport(player.getWorld().getSpawnLocation().add(Vector.getRandom()));
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                            i++;
                        } else {
                            player.kickPlayer("gn girl.");
                            cancel();
                        }
                    }
                }.runTaskTimer(ProjectR.getInstance(), 0, 0);
                return "Player got hypersofted";
            } else {
                return "Player doesn't exist";
            }
        }));
    }
}
