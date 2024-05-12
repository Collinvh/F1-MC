package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RaceTimer {
    private final BossBar bossBar;
    private final long length;
    private long currentLength;
    @Getter
    private boolean isFinished;
    @Getter @Setter
    private boolean isPaused;
    private boolean hasCountedPrevious;

    public RaceTimer(long length) {
        this.length = length;
        this.currentLength = this.length;
        bossBar = Bukkit.createBossBar(ChatColor.of("#e4e2df") + "Time Remaining: " + Utils.millisToTimeString(this.currentLength, "mm:ss"), BarColor.GREEN, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            addPlayer(onlinePlayer);
        }
    }

    public void addPlayer(Player player) {
        bossBar.addPlayer(player);
    }
    public void update() {
        if(isFinished) return;
        if(isPaused) return;
        if(hasCountedPrevious) {
            hasCountedPrevious = false;
            return;
        }
        if(currentLength < 60000) {
            bossBar.setColor(BarColor.RED);
        } else if(currentLength < 600000) {
            bossBar.setColor(BarColor.YELLOW);
        } else if(bossBar.getColor() != BarColor.GREEN) {
            bossBar.setColor(BarColor.GREEN);
        }
        currentLength -=1;
        if(currentLength <= 0) {
            isPaused = true;
            bossBar.setColor(BarColor.WHITE);
            bossBar.setTitle("Session finished");
            new BukkitRunnable() {
                @Override
                public void run() {
                    bossBar.removeAll();
                    RaceManager.setIsRunningTimer(false);
                    isFinished = true;
                }
            }.runTaskLater(F1MC.getInstance(), 600);
            return;
        }
        hasCountedPrevious = true;
        bossBar.setProgress((double) currentLength /length);
        bossBar.setTitle(ChatColor.of("#e4e2df") + "Time Remaining: " +Utils.millisToTimeString(this.currentLength, "mm:ss"));
    }

    public void stop() {
        isFinished = true;
        bossBar.removeAll();
        RaceManager.setIsRunningTimer(false);
    }

    public void setRed() {
        bossBar.setColor(BarColor.RED);
    }
}
