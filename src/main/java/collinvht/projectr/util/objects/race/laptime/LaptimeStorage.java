package collinvht.projectr.util.objects.race.laptime;

import collinvht.projectr.listener.MTListener;
import collinvht.projectr.manager.RacingManager;
import collinvht.projectr.util.objects.race.Race;
import collinvht.projectr.util.objects.race.RaceDriver;
import collinvht.projectr.util.objects.race.RaceListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class LaptimeStorage implements Cloneable {
    @Getter
    private final RaceDriver driver;
    private final UUID driverUUID;

    @Getter
    private final UUID lapUUID;

    @Getter
    private long laptime;

//    @Getter @Setter
//    private Tyres tyre = Tyres.NULLTYRE;

    @Getter
    private SectorData s1data;
    @Getter
    private SectorData s2data;
    @Getter
    private SectorData s3data;

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


    public LaptimeStorage(UUID driver, Race obj) {
        this.driver = MTListener.getRaceDrivers().get(driver);
        this.driverUUID = driver;
        this.race = obj;

        this.s1data = new SectorData(driver);
        this.s2data = new SectorData(driver);
        this.s3data = new SectorData(driver);
        this.lapData = new SectorData(driver);

        this.lapUUID = UUID.randomUUID();
    }

    public void createLaptime() {
        setLaptime(s1data.getSectorLength() + s2data.getSectorLength() + s3data.getSectorLength());
    }

    public ChatColor getS1Color() {
        if(RaceListener.getInstance().getBestS1() != -1) {
            if(RaceListener.getInstance().getBestS1() - s1data.getSectorLength() > 0) {
                s1data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                boolean faster = (driver.getLaptimes().getBestS1() - s1data.getSectorLength()) > 0;
                if(!faster) {
                    s1data.setSectorColor(ChatColor.YELLOW);
                    return ChatColor.YELLOW;
                }
            }
        } else {
            if(RaceListener.getInstance().getBestS1() == -1) {
                RaceListener.getInstance().setBestS1(s1data.getSectorLength());
                s1data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s1data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getS2Color() {
        if(RaceListener.getInstance().getBestS2() != -1) {
            if(RaceListener.getInstance().getBestS2() - s2data.getSectorLength() > 0) {
                s2data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                boolean faster = (driver.getLaptimes().getBestS2() - s2data.getSectorLength()) > 0;
                if(!faster) {
                    s2data.setSectorColor(ChatColor.YELLOW);
                    return ChatColor.YELLOW;
                }
            }
        } else {
            if(RaceListener.getInstance().getBestS2() == -1) {
                RaceListener.getInstance().setBestS2(s2data.getSectorLength());
                s2data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s2data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getS3Color() {
        if(RaceListener.getInstance().getBestS3() != -1) {
            if(RaceListener.getInstance().getBestS3() - s3data.getSectorLength() > 0) {
                s3data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                boolean faster = (RaceListener.getInstance().getBestS3() - s3data.getSectorLength()) > 0;
                if(!faster) {
                    s3data.setSectorColor(ChatColor.YELLOW);
                    return ChatColor.YELLOW;
                }
            }
        } else {
            if(RaceListener.getInstance().getBestS3() == -1) {
                RaceListener.getInstance().setBestS3(s3data.getSectorLength());
                s3data.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            }
        }
        s3data.setSectorColor(ChatColor.GREEN);
        return ChatColor.GREEN;
    }

    public ChatColor getLapColor(boolean setTime) {
        if(RaceListener.getInstance().getBestLapTime() != null) {
            long sectors = s1data.getSectorLength() + s2data.getSectorLength() + s3data.getSectorLength();
            if (RaceListener.getInstance().getBestLapTime().getLapData().getSectorLength() - sectors >= 0) {
                if(setTime) RaceListener.getInstance().setBestLapTime(this.clone());
                lapData.setSectorColor(ChatColor.LIGHT_PURPLE);
                return ChatColor.LIGHT_PURPLE;
            } else {
                boolean faster = (driver.getLaptimes().getFastestLap().getLaptime() - sectors) >= 0;
                if(!faster) {
                    lapData.setSectorColor(ChatColor.YELLOW);
                    if(setTime) return ChatColor.YELLOW;
                }
            }
        } else {
            if(setTime) RaceListener.getInstance().setBestLapTime(this.clone());
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
        if(s1data.getSectorLength() < RaceListener.getInstance().getBestS1()) {
            RaceListener.getInstance().setBestS1(s1data.getSectorLength());
        }
        if(s1data.getSectorLength() < driver.getLaptimes().getBestS1()) {
            driver.getLaptimes().setBestS1(s1data.getSectorLength());
        }


        if(s2data.getSectorLength() < RaceListener.getInstance().getBestS2()) {
            RaceListener.getInstance().setBestS2(s2data.getSectorLength());
        }
        if(s2data.getSectorLength() < driver.getLaptimes().getBestS2()) {
            driver.getLaptimes().setBestS2(s2data.getSectorLength());
        }

        if(s3data.getSectorLength() < RaceListener.getInstance().getBestS3()) {
            RaceListener.getInstance().setBestS3(s3data.getSectorLength());
        }
        if(s3data.getSectorLength() < driver.getLaptimes().getBestS3()) {
            driver.getLaptimes().setBestS3(s3data.getSectorLength());
        }


        this.laptime = laptime;

        this.lapData.setSectorLength(this.laptime);
    }

    public LaptimeStorage clone () {
        LaptimeStorage obj = new LaptimeStorage(driverUUID, race);
        obj.laptime = laptime;
        obj.s1data = s1data.clone();
        obj.s2data = s2data.clone();
        obj.s3data= s3data.clone();
        obj.lapData = lapData.clone();
//        obj.tyre = tyre;
        return obj;
    }
}
