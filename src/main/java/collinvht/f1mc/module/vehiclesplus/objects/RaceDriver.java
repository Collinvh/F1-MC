package collinvht.f1mc.module.vehiclesplus.objects;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.slowdown.manager.SlowdownManager;
import collinvht.f1mc.module.racing.object.laptime.DriverLaptimeStorage;
import collinvht.f1mc.module.racing.object.laptime.LaptimeStorage;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.racing.object.race.RaceListener;
import collinvht.f1mc.module.racing.object.race.RaceMode;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.util.Utils;
import ia.m.U;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.swing.text.html.Option;
import java.util.*;

public class RaceDriver {

    private final ComponentSidebarLayout sidebarComponent;
    @Setter @Getter
    private SpawnedVehicle vehicle;
    @Getter
    private boolean isDriving = true;

    private final RaceDriver instace;
    @Getter
    private final HashMap<Race, DriverLaptimeStorage> laptimes = new HashMap<>();

    @Getter
    private boolean isPassedPitExit;

    @Getter
    private boolean isInPit = true;

    @Getter @Setter
    private boolean finished;

    @Getter @Setter
    private long finishTime;

    @Getter @Setter
    private int currentLap;

    @Getter
    private final UUID driverUUID;
    @Getter
    private final String driverName;

    @Getter @Setter
    private boolean disqualified;

    @Getter @Setter
    private int speedingFlags;

    @Getter @Setter
    private Sidebar sidebar;

    public RaceDriver(Player player) {
        Bukkit.getLogger().warning(String.valueOf(player.getUniqueId()));
        this.driverUUID = player.getUniqueId();
        this.driverName = player.getName();
        initialize();
        SidebarComponent component = SidebarComponent.builder()
                .addBlankLine()
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(true, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        return Component.text(getNextPos(false, race));
                    }
                    return Component.text("");
                })
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        if(getLaptimes(race).getCurrentLap() != null) {
                            if(!youAreOnIt) {
                                if(p1Driver == null) return Component.text("");
                                return Component.text(player.getName() + " | " + Utils.millisToTimeString(p1Driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength() - curDriver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()));
                            }
                        }
                    }
                    return Component.text("");
                })
                .addBlankLine()
                .addDynamicLine(() -> {
                    Race race = RaceManager.getInstance().getRaceForPlayer(player);
                    if(race != null) {
                        if(getLaptimes(race).getCurrentLap() != null) {
                            LaptimeStorage laptimeStorage = getLaptimes(race).getCurrentLap();
                            return Component.text(Utils.millisToTimeString(System.currentTimeMillis() - laptimeStorage.getS1().getSectorStart(), "mm:ss"));
                        }
                    }
                    return Component.text("");
                }).build();
        SidebarComponent title = SidebarComponent.staticLine(Component.text(ChatColor.RED + "F1" + ChatColor.GRAY + "-MC"));
        this.sidebarComponent = new ComponentSidebarLayout(title, component);
//        this.sidebar = Utils.getScoreboardLibrary().createSidebar();
        this.instace = this;
    }

    public void tick() {
//        sidebarComponent.apply(sidebar);
    }

    private static TimerTask task;
    private static Timer timer;

    private void initialize() {
        task = new TimerTask() {
            @Override
            public void run() {
                if(isDriving) {
                    for (Race race : RaceListener.getLISTENING()) {
                        race.getRaceLapStorage().update(instace);
                    }
                    try {
                        Bukkit.getPlayer(getDriverUUID()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Speed: " + vehicle.getCurrentSpeedInKm() + " | Fuel: " + vehicle.getStorageVehicle().getVehicleStats().getCurrentFuel()));
                    } catch (Exception ignored) {}
                }
            }
        };
        if(timer == null) timer = new Timer("F1MC RaceListener Player | " + driverName);
        timer.schedule(task, 0, 1);
    }

    private RaceDriver p1Driver;
    private RaceDriver curDriver;
    private boolean youAreOnIt;
    private ListOrderedMap<RaceDriver, Long> treeMap;
    public String getNextPos(boolean restartList, Race race) {
        if(restartList) {
            treeMap = null;
            curDriver = null;
        }
        if(treeMap == null) {
            HashMap<UUID, RaceDriver> drivers = VPListener.getRACE_DRIVERS();
            if(!drivers.isEmpty()) {
                LinkedHashMap<RaceDriver, Long> sectors = new LinkedHashMap<>();
                drivers.forEach((unused, driver) -> {
                    if (driver.getLaptimes(race).getFastestLap() != null) {
                        sectors.put(driver, driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength());
                    }
                });
                treeMap = Utils.sortByValueDesc(sectors);
            }
        }
        if(treeMap != null) {
            if(curDriver == null && restartList) {
                Optional<Map.Entry<RaceDriver, Long>> firstDriver = treeMap.entrySet().stream().findFirst();
                if (firstDriver.isPresent()) {
                    curDriver = firstDriver.get().getKey();
                    OfflinePlayer player = Bukkit.getOfflinePlayer(curDriver.getDriverUUID());
                    if (p1Driver == null) {
                        p1Driver = curDriver;
                        youAreOnIt = curDriver.getDriverUUID().equals(driverUUID);
                        return (player.getName() + " | " + Utils.millisToTimeString(curDriver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()));
                    } else {
                        youAreOnIt = curDriver.getDriverUUID().equals(driverUUID);
                        if(p1Driver == curDriver) return (player.getName() + Utils.millisToTimeString(curDriver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()));
                        return (player.getName() + " | " + Utils.millisToTimeString(p1Driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength() - curDriver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()));
                    }
                }
            } else {
                curDriver = treeMap.nextKey(curDriver);
                if(curDriver == null) return "";
                OfflinePlayer player = Bukkit.getOfflinePlayer(curDriver.getDriverUUID());
                if (p1Driver == null) {
                    p1Driver = curDriver;
                    youAreOnIt = curDriver.getDriverUUID().equals(driverUUID);
                    return (player.getName() + " |  " + Utils.millisToTimeString(curDriver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()));
                } else {
                    youAreOnIt = curDriver.getDriverUUID().equals(driverUUID);
                    if(p1Driver == curDriver) return player.getName() + Utils.millisToTimeString(curDriver.getLaptimes(race).getFastestLap().getLapData().getSectorLength());
                    return (player.getName() + " | " + Utils.millisToTimeString(p1Driver.getLaptimes(race).getFastestLap().getLapData().getSectorLength() - curDriver.getLaptimes(race).getFastestLap().getLapData().getSectorLength()));
                }
            }
        }
        return "";
    }

    public DriverLaptimeStorage getLaptimes(Race race) {
        if(!laptimes.containsKey(race)) {
            laptimes.put(race, new DriverLaptimeStorage(race));
        }
        return laptimes.get(race);
    }

    public void addLaptime(Race race, RaceMode mode, LaptimeStorage storage) {
        laptimes.get(race).addLaptime(storage, mode);
    }

    public void setDriving(boolean driving) {
        isDriving = driving;
        isPassedPitExit = false;
        Player player = Bukkit.getPlayer(driverUUID);
        if(player != null) {
            if (driving) {
                if (RaceManager.getDrivingPlayers().containsKey(player)) {
//                    sidebar.addPlayer(player);
                }
            } else {
//                sidebar.removePlayer(player);
            }
        }
    }

    public void setInPit() {
        isInPit = true;
        isPassedPitExit = false;
    }

    public void setPassedPitExit(Race race) {
        isInPit = false;
        isPassedPitExit = true;
        if(laptimes.get(race) != null) {
            if (laptimes.get(race).getCurrentLap() != null) {
                laptimes.get(race).getCurrentLap().getS1().setSectorStart(System.currentTimeMillis() - 1000);
            }
        }
    }

    public void reset() {
        currentLap = 0;
    }

    public void delete() {
        task.cancel();
    }
}
