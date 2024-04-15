package collinvht.f1mc.module.racing.object.laptime;

import collinvht.f1mc.module.discord.DiscordModule;
import collinvht.f1mc.module.racing.object.NamedCuboid;
import collinvht.f1mc.module.vehiclesplus.listener.listeners.VPListener;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.module.vehiclesplus.objects.RaceDriver;
import collinvht.f1mc.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class LaptimeStorage {
    @Getter
    private final RaceDriver driver;
    @Getter
    private final UUID driverUUID;

    @Getter
    private final UUID lapUUID;

    @Getter
    private long laptime;

    @Getter
    private SectorData s1data;
    @Getter
    private final HashMap<Integer, SectorData> s1_minis = new HashMap<>();
    @Getter
    private SectorData s2data;
    @Getter
    private final HashMap<Integer, SectorData> s2_minis = new HashMap<>();
    @Getter
    private SectorData s3data;
    @Getter
    private final HashMap<Integer, SectorData> s3_minis = new HashMap<>();

    @Getter @Setter
    private boolean passedS1;

    @Getter @Setter
    private boolean passedS2;

    @Getter @Setter
    private boolean passedS3;

    @Getter @Setter
    private boolean pastPitExit;

    @Getter
    private SectorData lapData;

    @Getter
    private final Race race;


    public LaptimeStorage(RaceDriver driver, Race obj) {
        this.driver = driver;
        this.driverUUID = driver.getDriverUUID();
        this.race = obj;

        this.s1data = new SectorData(driver.getDriverUUID());
        this.s2data = new SectorData(driver.getDriverUUID());
        this.s3data = new SectorData(driver.getDriverUUID());
        this.lapData = new SectorData(driver.getDriverUUID());

        this.lapUUID = UUID.randomUUID();
    }

    public void createLaptime() {
        setLaptime(s1data.getSectorLength() + s2data.getSectorLength() + s3data.getSectorLength());

        if(Utils.isEnableDiscordModule()) {
            DiscordModule module = DiscordModule.getInstance();
            if (module.isInitialized()) {
                TextChannel channel = module.getJda().getTextChannelById(1217628051853021194L);
                if (channel == null) return;
                EmbedBuilder builder = new EmbedBuilder();
                builder.addField("New laptime at " + race.getName(), "Driven by" + driver.getDriverName(), true);
                builder.addBlankField(true);
                builder.setColor(Color.BLUE);
                builder.addField("Time:", Utils.millisToTimeString(getLaptime()), false);
                channel.sendMessage(builder.build()).queue();
            }
        }
    }

    public ChatColor getS1Color() {
        if(race.getRaceLapStorage().getBestS1() != -1) {
            long differenceToFastest = race.getRaceLapStorage().getBestS1() - s1data.getSectorLength();
            s1data.setSectorDifference(differenceToFastest);
            if(differenceToFastest > 0) {
                s1data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                long val = driver.getLaptimes(race).getBestS1() - s1data.getSectorLength();
                boolean faster = val > 0;
                if(!faster) {
                    if(val < -1000) {
                        s1data.setSectorColor(ChatColor.RED);
                        return ChatColor.RED;
                    } else {
                        s1data.setSectorColor(ChatColor.YELLOW);
                        return ChatColor.YELLOW;
                    }
                }
            }
        } else {
            if(race.getRaceLapStorage().getBestS1() == -1) {
                race.getRaceLapStorage().setBestS1(s1data.getSectorLength());
                s1data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s1data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getS2Color() {
        if(race.getRaceLapStorage().getBestS2() != -1) {
            long differenceToFastest = race.getRaceLapStorage().getBestS2() - s2data.getSectorLength();
            s2data.setSectorDifference(differenceToFastest);
            if(differenceToFastest > 0) {
                s2data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                long val = driver.getLaptimes(race).getBestS2() - s2data.getSectorLength();
                boolean faster = val > 0;
                if(!faster) {
                    if(val < -1000) {
                        s2data.setSectorColor(ChatColor.RED);
                        return ChatColor.RED;
                    } else {
                        s2data.setSectorColor(ChatColor.YELLOW);
                        return ChatColor.YELLOW;
                    }
                }
            }
        } else {
            if(race.getRaceLapStorage().getBestS2() == -1) {
                race.getRaceLapStorage().setBestS2(s2data.getSectorLength());
                s2data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s2data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getS3Color() {
        if(race.getRaceLapStorage().getBestS3() != -1) {
            long differenceToFastest = race.getRaceLapStorage().getBestS3() - s3data.getSectorLength();
            s3data.setSectorDifference(differenceToFastest);
            if(differenceToFastest > 0) {
                s3data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                long val = driver.getLaptimes(race).getBestS3() - s3data.getSectorLength();
                boolean faster = val > 0;
                if(!faster) {
                    if(val < -1000) {
                        s3data.setSectorColor(ChatColor.RED);
                        return ChatColor.RED;
                    } else {
                        s3data.setSectorColor(ChatColor.YELLOW);
                        return ChatColor.YELLOW;
                    }
                }
            }
        } else {
            if(race.getRaceLapStorage().getBestS3() == -1) {
                race.getRaceLapStorage().setBestS3(s3data.getSectorLength());
                s3data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s3data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getLapColor(boolean setTime) {
        if(race.getRaceLapStorage().getBestLapTime() != null) {
            long sectors = s1data.getSectorLength() + s2data.getSectorLength() + s3data.getSectorLength();
            long difference = race.getRaceLapStorage().getBestLapTime().getLapData().getSectorLength() - sectors;
            lapData.setSectorDifference(difference);
            if (difference >= 0) {
                if(setTime) race.getRaceLapStorage().setBestLapTime(this.copy());
                lapData.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                long val = (driver.getLaptimes(race).getFastestLap().getLaptime() - sectors);
                boolean faster = val >= 0;
                if(!faster) {
                    if(val < -1000) {
                        lapData.setSectorColor(ChatColor.RED);
                        return ChatColor.RED;
                    } else {
                        lapData.setSectorColor(ChatColor.YELLOW);
                        return ChatColor.YELLOW;
                    }
                }
            }
        } else {
            if(setTime) race.getRaceLapStorage().setBestLapTime(this.copy());
            lapData.setSectorColor(ChatColor.LIGHT_PURPLE);
            return ChatColor.LIGHT_PURPLE;
        }
        lapData.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public void setS1(long s1) {
        this.s2data.setSectorStart(s1);
        this.s1data.setSectorLength(s1);
    }

    public void setS2(long s2) {
        this.s3data.setSectorStart(s2);
        this.s2data.setSectorLength(s2);
    }

    public void setS3(long s3) {
        this.s1data.setSectorStart(s3);
        this.s3data.setSectorLength(s3);
    }

    private void setLaptime(long laptime) {
        if(s1data.getSectorLength() < race.getRaceLapStorage().getBestS1()) {
            race.getRaceLapStorage().setBestS1(s1data.getSectorLength());
        }
        if(s1data.getSectorLength() < driver.getLaptimes(race).getBestS1()) {
            driver.getLaptimes(race).setBestS1(s1data.getSectorLength());
        }


        if(s2data.getSectorLength() < race.getRaceLapStorage().getBestS2()) {
            race.getRaceLapStorage().setBestS2(s2data.getSectorLength());
        }
        if(s2data.getSectorLength() < driver.getLaptimes(race).getBestS2()) {
            driver.getLaptimes(race).setBestS2(s2data.getSectorLength());
        }

        if(s3data.getSectorLength() < race.getRaceLapStorage().getBestS3()) {
            race.getRaceLapStorage().setBestS3(s3data.getSectorLength());
        }
        if(s3data.getSectorLength() < driver.getLaptimes(race).getBestS3()) {
            driver.getLaptimes(race).setBestS3(s3data.getSectorLength());
        }


        this.laptime = laptime;
        if(driver.getLaptimes(race).getFastestLap() != null) {
            this.lapData.setSectorDifference(driver.getLaptimes(race).getFastestLap().laptime - this.laptime);
        } else {
            this.lapData.setSectorDifference(0);
        }

        this.lapData.setSectorLength(this.laptime);
    }

    public LaptimeStorage copy () {
        LaptimeStorage obj = new LaptimeStorage(driver, race);
        obj.laptime = laptime;
        obj.s1data = s1data.copy();
        obj.s2data = s2data.copy();
        obj.s3data= s3data.copy();
        obj.lapData = lapData.copy();
        return obj;
    }
}
