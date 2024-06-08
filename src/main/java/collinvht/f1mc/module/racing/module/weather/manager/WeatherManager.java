package collinvht.f1mc.module.racing.module.weather.manager;

import collinvht.f1mc.F1MC;
import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTransitionSpeed;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTypes;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.util.DefaultMessages;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.joml.Math;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WeatherManager {
    private static final String prefix = DefaultMessages.PREFIX;
    private static ScheduledTask weatherTask;
    private static final Random rng = new Random();

    public static String getForecast(String raceName) {
        Race race = RaceManager.getRACES().get(raceName);
        if(race == null) return prefix + "That race doesn't exist";
        if(race.getRaceLapStorage() == null) return prefix + "The race has not been started";
        double percentage = race.getRaceLapStorage().getWaterPercentage();
        return prefix + "We expect it to be " + WeatherTypes.fromPercentageAprox(percentage).getName() + " for now.";
    }
    public static String getWeather(String raceName) {
        Race race = RaceManager.getRACES().get(raceName);
        if(race == null) return prefix + "That race doesn't exist";
        if(race.getRaceLapStorage() == null) return prefix + "Race storage is null";
        return prefix + race.getRaceLapStorage().getWeatherType().getName();
    }
    public static String setWeather(String raceName) {
        return setWeather(raceName, WeatherTypes.random());
    }
    public static String setWeather(String raceName, String weatherType) {
        return setWeather(raceName, weatherType, WeatherTransitionSpeed.random());
    }
    public static String setWeather(String raceName, String weatherType, String weatherTransitionSpeed) {
        return setWeather(rng.nextLong(200), raceName, weatherType, weatherTransitionSpeed);
    }
    public static String setWeather(long delay, String raceName, String weatherType, String weatherTransitionSpeed) {
        Race race = RaceManager.getRACES().get(raceName);
        if(race == null) return prefix + "That race doesn't exist";
        WeatherTypes type = WeatherTypes.from(weatherType);
        if(type == null) return prefix + "That weather type doesn't exist";
        WeatherTransitionSpeed transitionSpeed = WeatherTransitionSpeed.from(weatherTransitionSpeed);
        if(transitionSpeed == null) return prefix + "That transition type doesn't exist";
        if(race.getRaceLapStorage() == null) return prefix + "Theirs no race storage";
        if(race.getRaceLapStorage().getWeatherType().equals(type)) return prefix + "This weather type is already the current weather type";
        race.getRaceLapStorage().setNextWeatherType(type);
        if(transitionSpeed == WeatherTransitionSpeed.INSTANT) {
            race.getRaceLapStorage().setWaterPercentage(type.getWaterPercentage());
        } else {
            weatherTask = F1MC.getAsyncScheduler().runAtFixedRate(F1MC.getInstance(), scheduledTask -> {
                double addedAmount = transitionSpeed.getExtraTransitionTicks();
                int goal = type.getWaterPercentage();
                double waterPercentage = race.getRaceLapStorage().getWaterPercentage();
                if(waterPercentage > type.getWaterPercentage()) {
                    double value = Math.max(waterPercentage - addedAmount, 0);
                    race.getRaceLapStorage().setWaterPercentage(Math.max(value, goal));
                    F1MC.getLog().warning(String.valueOf(waterPercentage - addedAmount));
                } else {
                    double value = Math.min(waterPercentage + addedAmount, 100);
                    race.getRaceLapStorage().setWaterPercentage(Math.min(value, goal));
                    F1MC.getLog().warning(String.valueOf(waterPercentage + addedAmount));
                }
                if(race.getRaceLapStorage().getWaterPercentage() == type.getWaterPercentage()) {
                    race.getRaceLapStorage().setWeatherType(type);
                    weatherTask.cancel();
                }
            }, 0, 500, TimeUnit.MILLISECONDS);
        }
        return prefix + "Weather is being adjusted";
    }

    public static double[] currentRotation(Race race) {
        WeatherTypes current = race.getRaceLapStorage().getWeatherType();
        WeatherTypes next = WeatherTypes.fromPercentageAprox(race.getRaceLapStorage().getWaterPercentage());
        double[] doubles = new double[3];
        if(next != null) {
            if(current != next) {
                float calculation;
                float currentPercentage = Math.min(1, current.getWaterPercentage());
                float difference = Math.min(1, Math.abs(current.getWaterPercentage()-next.getWaterPercentage())); //45 = 100%
                calculation = difference > currentPercentage ? currentPercentage/difference : difference/currentPercentage;

                doubles[0] = Math.lerp(current.getInterSpeedMultiplier(), next.getInterSpeedMultiplier(), calculation);
                doubles[1] = Math.lerp(current.getWetSpeedMultiplier(), next.getWetSpeedMultiplier(), calculation);
                doubles[2] = Math.lerp(current.getSlickSpeedMultiplier(), next.getSlickSpeedMultiplier(), calculation);
                Bukkit.getLogger().warning(doubles[0] + " | " + doubles[1] + " | " + doubles[2] + " | " + next.getName() + " | " + current.getName() + " | " + calculation);
            } else {
                doubles[0] = current.getInterSpeedMultiplier();
                doubles[1] = current.getWetSpeedMultiplier();
                doubles[2] = current.getSlickSpeedMultiplier();
            }
        } else {
            Bukkit.getLogger().severe("Theirs been an issue getting the currentRotation for the tyres");
            doubles[0] = WeatherTypes.DRY.getInterSpeedMultiplier();;
            doubles[1] = WeatherTypes.DRY.getWetSpeedMultiplier();;
            doubles[2] = WeatherTypes.DRY.getSlickSpeedMultiplier();;
        }
        return doubles;
    }
}
