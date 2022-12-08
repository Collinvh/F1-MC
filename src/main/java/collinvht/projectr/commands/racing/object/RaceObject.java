package collinvht.projectr.commands.racing.object;

import collinvht.projectr.commands.fia.Warning;
import collinvht.projectr.commands.racing.SnelsteCommand;
import collinvht.projectr.commands.racing.laptime.LaptimeListener;
import collinvht.projectr.commands.racing.laptime.object.Laptime;
import collinvht.projectr.listener.driver.DriverManager;
import collinvht.projectr.listener.driver.object.DriverObject;
import collinvht.projectr.listener.driver.object.FinishData;
import collinvht.projectr.manager.tyre.Tyres;
import collinvht.projectr.util.objs.DiscordUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RaceObject {

    @Getter @Setter
    private String raceName;

    @Getter @Setter
    private int lapCount;

    @Getter
    private final ArrayList<FinishData> finishedDrivers = new ArrayList<>();

    @Getter
    private RaceMode runningMode = RaceMode.TRAINING;

    @Getter @Setter
    private boolean disabled;

    @Getter
    private boolean isRunning;

    @Getter @Setter
    private CuboidStorage storage = new CuboidStorage();

    @Getter
    private LaptimeListener listener = new LaptimeListener(this);

    @Getter @Setter
    private int pitSpeed = 80;

    public RaceObject(String raceName, int lapCount) {
        this.raceName = raceName;
        this.lapCount = lapCount;
    }

    public void startRace(RaceMode mode) {
        this.runningMode = mode;
        this.isRunning = true;

        listener.startTiming();
    }

    public void stopRace(boolean runDiscord) {
        listener.stopTiming();

        this.isRunning = false;

        if(runDiscord) {

            JDA jda = DiscordUtil.getJda();
            jda.getPresence().setActivity(Activity.streaming("Project R", "https://discord.gg/ykXmbNgA7X"));
            jda.getPresence().setStatus(OnlineStatus.ONLINE);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Race result", null);
            embedBuilder.setDescription(getRaceName());
            embedBuilder.setColor(new Color(23, 213, 187));

            if (runningMode.isHasLaps()) {

                ArrayList<FinishData> data = getFinishedDrivers();

                if (data.size() > 0 && getListener().getBestLapTime() != null) {

                    data.sort(Comparator.comparingDouble(FinishData::getFinishPosition));

                    StringBuilder builder = new StringBuilder();
                    for (FinishData d : data) {
                        builder.append(d.getFinishPosition() + "." + " : " + d.getDriver().getPlayer().getName() + "\n");
                    }

                    embedBuilder.addField("Finish posities", builder.toString(), false);


                    embedBuilder.addField("Fastest lap : ", Laptime.millisToTimeString(getListener().getBestLapTime().getLapData().getSectorLength()), true);
                }

            } else {
                LinkedHashMap<DriverObject, Long> sectors = new LinkedHashMap<>();


                HashMap<UUID, DriverObject> drivers = DriverManager.getDrivers();

                drivers.forEach((unused, driver) -> {
                    if (driver.getLapstorage().getBestTime() != null) {
                        sectors.put(driver, driver.getLapstorage().getBestTime().getLaptime());
                    }
                });

                LinkedHashMap<DriverObject, Long> treeMap = SnelsteCommand.sortByValueDesc(sectors);

                StringBuilder builder = new StringBuilder();

                AtomicInteger pos = new AtomicInteger();
                treeMap.forEach((driver, aLong) -> {
                    pos.getAndIncrement();
                    if (driver.getLapstorage().getBestTime() != null) {
                        String tyre;
                        if (driver.getLapstorage().getBestTime().getTyre() != Tyres.NULLTYRE) {
                            tyre = " [" + driver.getLapstorage().getBestTime().getTyre().getName().charAt(0) + "]";
                        } else {
                            tyre = " [" + ChatColor.BLACK + "?" + ChatColor.RESET + "]";
                        }

                        builder.append(pos.get() + "." + " : " + driver.getPlayer().getName() + "  : " + Laptime.millisToTimeString(driver.getLapstorage().getBestTime().getLapData().getSectorLength()) + tyre + "\n");
                    }
                });

                embedBuilder.addField("Posities", builder.toString(), false);
            }


            embedBuilder.setFooter("Project R");

            DiscordUtil.getChannelByID(844159011666526208L).sendMessage(embedBuilder.build()).queue();
        }

        this.runningMode = null;
        listener = new LaptimeListener(this);
    }

    public void resetRace() {
        Warning.setWarningCount(new HashMap<>());
        DriverManager.getDrivers().forEach((uuid, object) -> object.resetStorage());
        listener.stopTiming();
        listener = new LaptimeListener(this);
        listener.startTiming();
    }

    public void addFinishedDriver(FinishData data) {
        finishedDrivers.add(data);
    }
}
