package collinvht.zenticracing.util.objs;

import lombok.Getter;

public enum WeatherType {
    NORMAAL("normaal", 50, 145, 130, org.bukkit.WeatherType.DOWNFALL),
    ZACHT("zacht", 10, 140, 80, org.bukkit.WeatherType.DOWNFALL),
    HEFTIG("heftig", 70, 130, 140, org.bukkit.WeatherType.DOWNFALL),
    STORM("storm", 100, 80, 100, org.bukkit.WeatherType.DOWNFALL),
    OFF("off", 0, -1, -1, org.bukkit.WeatherType.CLEAR);


    @Getter
    private final String naam;
    @Getter
    private final int dry;
    @Getter
    private final int inter;
    @Getter
    private final int wet;
    @Getter
    private final org.bukkit.WeatherType type;
    WeatherType(String naam, int dry, int inter, int wet, org.bukkit.WeatherType type) {
        this.naam = naam;
        this.dry = dry;
        this.inter = inter;
        this.wet = wet;
        this.type = type;
    }
}
