package collinvht.f1mc.module.racing.module.weather.obj;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

@Getter
public enum WeatherTypes {
    EXTREME(0, "extreme",100, 0.4, 0.15, 0.2),
    STORM(1, "storm", 80, 0.75, 0.5, 0.4),
    HEAVY_RAIN(2, "heavy_rain", 60, 1, 0.7, 0.5),
    RAIN(3, "rain", 45, 0.75, 1, 0.65),
    LIGHT_RAIN(4, "light_rain", 20, 0.65, 0.9, 0.8),
    DRIZZLE(5, "drizzle", 10, 0.4, 0.6, 0.9),
    DRY(6, "dry",0, 0.4, 0.5, 1);

    private final String name;
    private final int id;
    private final int waterPercentage;
    private final double wetSpeedMultiplier;
    private final double interSpeedMultiplier;
    private final double slickSpeedMultiplier;
    WeatherTypes(int id, String name, int waterPercentage, double wetSpeedMultiplier, double interSpeedMultiplier, double slickSpeedMultiplier) {
        this.id = id;
        this.name = name;
        this.waterPercentage = waterPercentage;
        this.wetSpeedMultiplier = wetSpeedMultiplier;
        this.interSpeedMultiplier = interSpeedMultiplier;
        this.slickSpeedMultiplier = slickSpeedMultiplier;
    }

    public static WeatherTypes from(String weatherType) {
        for (WeatherTypes value : values()) {
            if(value.name.equalsIgnoreCase(weatherType)) return value;
        }
        return null;
    }

    public static String random() {
        return Arrays.stream(values()).findAny().get().name;
    }

    public static WeatherTypes fromPercentageAprox(double percentage) {
        try {
            return Arrays.stream(values()).min(Comparator.comparingDouble(i -> Math.abs(i.getWaterPercentage() - percentage))).orElseThrow(() -> new NoSuchElementException("No value present"));
        } catch (NoSuchElementException e) {
            return WeatherTypes.DRY;
        }
    }

    public static WeatherTypes fromID(int id) {
        for (WeatherTypes value : WeatherTypes.values()) {
            if(value.id == id) return value;
        }
        return null;
    }
}
