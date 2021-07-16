package collinvht.zenticracing.commands.racing.laptime.object;

import collinvht.zenticracing.commands.racing.object.RaceObject;
import collinvht.zenticracing.listener.driver.object.DriverObject;
import collinvht.zenticracing.manager.tyre.Tyres;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Laptime {
    @Getter
    private final DriverObject driver;

    @Getter
    private final UUID lapUUID;

    @Getter
    private long laptime;

    @Getter @Setter
    private Tyres tyre = Tyres.NULLTYRE;

    @Getter
    private SectorData s1data;
    @Getter
    private SectorData s2data;
    @Getter
    private SectorData s3data;


    @Getter
    private SectorData lapData;


    @Getter @Setter
    private long s1s = -1;
    @Getter
    private long s1;

    @Getter
    private long s2s;
    @Getter
    private long s2;

    @Getter
    private long s3s;
    @Getter
    private long s3;

    @Getter
    private final RaceObject race;


    public Laptime(DriverObject driver, RaceObject obj) {
        this.driver = driver;
        this.race = obj;
        this.s1data = new SectorData(driver);
        this.s2data = new SectorData(driver);
        this.s3data = new SectorData(driver);
        this.lapData = new SectorData(driver);

        this.lapUUID = UUID.randomUUID();
    }

    public void createLaptime() {
        setLaptime(s1 + s2 + s3);
    }

    public static String millisToTimeString(final long mSec) {
        final String pattern = "mm:ss.SSS";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(mSec));
    }

    public ChatColor getS1Color() {
        if(race.getListener().getBestS1() != -1) {
            if(race.getListener().getBestS1() - s1 > 0) {
                s1data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                boolean faster = (driver.getLapstorage().getBestS1() - s1) > 0;
                if(!faster) {
                    s1data.setSectorColor(ChatColor.YELLOW);
                    return ChatColor.YELLOW;
                }
            }
        } else {
            if(race.getListener().getBestS1() == -1) {
                race.getListener().setBestS1(s1);
                s1data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s1data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getS2Color() {
        if(race.getListener().getBestS2() != -1) {
            if(race.getListener().getBestS2() - s2 > 0) {
                s2data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                boolean faster = (driver.getLapstorage().getBestS2() - s2) > 0;
                if(!faster) {
                    s2data.setSectorColor(ChatColor.YELLOW);
                    return ChatColor.YELLOW;
                }
            }
        } else {
            if(race.getListener().getBestS2() == -1) {
                race.getListener().setBestS2(s2);
                s2data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s2data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getS3Color() {
        if(race.getListener().getBestS3() != -1) {
            if(race.getListener().getBestS3() - s3 > 0) {
                s3data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                boolean faster = (driver.getLapstorage().getBestS3() - s3) > 0;
                if(!faster) {
                    s3data.setSectorColor(ChatColor.YELLOW);
                    return ChatColor.YELLOW;
                }
            }
        } else {
            if(race.getListener().getBestS3() == -1) {
                race.getListener().setBestS3(s3);
                s3data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s3data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getLapColor(boolean setTime) {
        if(race.getListener().getBestLapTime() != null) {
            long sectors = s1 + s2 + s3;
            if (race.getListener().getBestLapTime().getLapData().getSectorLength() - sectors >= 0) {
                if(setTime) race.getListener().setBestLapTime(this.clone());
                lapData.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                boolean faster = (driver.getLapstorage().getBestTime().getLaptime() - sectors) >= 0;
                if(!faster) {
                    lapData.setSectorColor(ChatColor.YELLOW);
                    if(setTime) return ChatColor.YELLOW;
                }
            }
        } else {
            if(setTime) race.getListener().setBestLapTime(this.clone());
            lapData.setSectorColor(ChatColor.LIGHT_PURPLE);
            return ChatColor.LIGHT_PURPLE;
        }
        lapData.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public void setS1(long s1) {
        this.s2s = s1;
        this.s1 = s1 - s1s;
        this.s1data.setSectorLength(this.s1);
    }

    public void setS2(long s2) {
        this.s3s = s2;
        this.s2 = s2 - s2s;
        this.s2data.setSectorLength(this.s2);
    }

    public void setS3(long s3) {
        this.s1s = s3;
        this.s3 = s3 - s3s;
        this.s3data.setSectorLength(this.s3);
    }

    private void setLaptime(long laptime) {
        if(s1 < race.getListener().getBestS1()) {
            race.getListener().setBestS1(s1);
        }
        if(s1 < driver.getLapstorage().getBestS1()) {
            driver.getLapstorage().setBestS1(s1);
        }


        if(s2 < race.getListener().getBestS2()) {
            race.getListener().setBestS2(s2);
        }
        if(s2 < driver.getLapstorage().getBestS2()) {
            driver.getLapstorage().setBestS2(s2);
        }

        if(s3 < race.getListener().getBestS3()) {
            race.getListener().setBestS3(s3);
        }
        if(s3 < driver.getLapstorage().getBestS3()) {
            driver.getLapstorage().setBestS3(s3);
        }


        this.laptime = laptime;

        this.lapData.setSectorLength(this.laptime);
    }

    public Laptime clone () {
        Laptime obj = new Laptime(driver, race);
        obj.s1 = s1;
        obj.s2 = s2;
        obj.s3 = s3;
        obj.laptime = laptime;
        obj.s1data = s1data.clone();
        obj.s2data = s2data.clone();
        obj.s3data= s3data.clone();
        obj.lapData = lapData.clone();
        obj.tyre = tyre;
        return obj;
    }
}
