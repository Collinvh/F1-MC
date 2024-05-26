package collinvht.f1mc.module.racing.object.laptime;

import collinvht.f1mc.module.racing.object.NamedCuboid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
@Getter @Setter
public class LaptimeStorage {
    private ArrayList<NamedCuboid> cuboids = new ArrayList<>();
    private HashMap<String, SectorData> s1_minis = new HashMap<>();
    private SectorData s1;
    private HashMap<String, SectorData> s2_minis = new HashMap<>();
    private SectorData s2;
    private HashMap<String, SectorData> s3_minis = new HashMap<>();
    private SectorData s3;
    private SectorData lapData;
    private boolean passedS1;
    private boolean passedS2;
    private boolean passedS3;

    private String currentMini = "";

    private final UUID holder;

    public LaptimeStorage(UUID holder) {
        this.s1 = new SectorData(holder);
        this.s2 = new SectorData(holder);
        this.s3 = new SectorData(holder);
        this.lapData = new SectorData(holder);
        this.holder = holder;
    }

    public void setS1L(long s1) {
        this.s2.setSectorStart(s1);
        this.s1.setSectorLengthL(s1);
    }

    public void setS2L(long s2) {
        this.s3.setSectorStart(s2);
        this.s2.setSectorLengthL(s2);
    }

    public void setS3L(long s3) {
        this.s1.setSectorStart(s3);
        this.s3.setSectorLengthL(s3);
    }

    public void setLapL(long l) {
        this.lapData.setSectorLength(l);
    }

    public LaptimeStorage copy () {
        LaptimeStorage obj = new LaptimeStorage(holder);
        obj.lapData = lapData;
        obj.s1 = s1.copy();
        obj.s2 = s2.copy();
        obj.s3= s3.copy();
        obj.lapData = lapData.copy();
        return obj;
    }
}
