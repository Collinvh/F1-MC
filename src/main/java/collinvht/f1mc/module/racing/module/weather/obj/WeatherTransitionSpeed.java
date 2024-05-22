package collinvht.f1mc.module.racing.module.weather.obj;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum WeatherTransitionSpeed {
    SLOW(0, "slow", 1),
    NORMAL(1, "normal", 3),
    FAST(2, "fast", 6),
    INSTANT(3, "instant",100);

    private final int id;
    private final String name;
    private final int extraTransitionTicks;
    WeatherTransitionSpeed(int id, String name, int extraTransitionTicks) {
        this.id = id;
        this.name = name;
        this.extraTransitionTicks = extraTransitionTicks;
    }

    public static WeatherTransitionSpeed from(String weatherTransitionSpeed) {
        for (WeatherTransitionSpeed value : values()) {
            if(value.name.equalsIgnoreCase(weatherTransitionSpeed)) return value;
        }
        return null;
    }

    public static String random() {
        return Arrays.stream(values()).findAny().get().name;
    }
}
