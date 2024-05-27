package collinvht.f1mc.module.racing.module.weather.manager;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTransitionSpeed;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTypes;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.util.DefaultMessages;
import org.bukkit.Bukkit;
import org.joml.Math;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherManager {
    private static final String prefix = DefaultMessages.PREFIX;
    private static final Timer weatherTimer = new Timer();
    private static final Random rng = new Random();

    public static String getForecast(String raceName) {
        Race race = RaceManager.getRACES().get(raceName);
        if(race == null) return prefix + "That race doesn't exist";
        if(race.getRaceLapStorage() == null) return prefix + "The race has not been started";
        double percentage = race.getRaceLapStorage().getWaterPercentage();
        return prefix + "We expect it to be " + WeatherTypes.fromPercentageAprox(percentage) + " for now.";
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
            TimerTask timerTask = getWeatherTask(race, transitionSpeed, type);
            weatherTimer.schedule(timerTask, delay, 100);
        }
        return prefix + "Weather is being adjusted";
    }

    public static double[] currentRotation(Race race) {
        WeatherTypes current = race.getRaceLapStorage().getWeatherType();
        WeatherTypes next = race.getRaceLapStorage().getNextWeatherType();
        int idDifference = current.getId() - next.getId();
        if(idDifference != 1 && idDifference != -1) {
            if(idDifference < 0) {
                next = WeatherTypes.fromID(current.getId()-1);
            } else {
                next = WeatherTypes.fromID(current.getId()+1);
            }
        }
        double[] doubles = new double[3];
        if(next != null) {
            if(idDifference > 0) {
                double calculation = ((double) next.getWaterPercentage()) / ((double) current.getWaterPercentage());
                doubles[0] = Math.lerp(current.getInterSpeedMultiplier(), next.getInterSpeedMultiplier(), calculation);
                doubles[1] = Math.lerp(current.getWetSpeedMultiplier(), next.getWetSpeedMultiplier(), calculation);
                doubles[2] = Math.lerp(current.getSlickSpeedMultiplier(), next.getSlickSpeedMultiplier(), calculation);
            } else {
                double calculation = ((double) current.getWaterPercentage()) / ((double) next.getWaterPercentage());
                doubles[0] = Math.lerp(current.getInterSpeedMultiplier(), next.getInterSpeedMultiplier(), calculation);
                doubles[1] = Math.lerp(current.getWetSpeedMultiplier(), next.getWetSpeedMultiplier(), calculation);
                doubles[2] = Math.lerp(current.getSlickSpeedMultiplier(), next.getSlickSpeedMultiplier(), calculation);
            }
        } else {
            Bukkit.getLogger().severe("Theirs been an issue getting the currentRotation for the tyres");
            doubles[0] = WeatherTypes.DRY.getInterSpeedMultiplier();;
            doubles[1] = WeatherTypes.DRY.getWetSpeedMultiplier();;
            doubles[2] = WeatherTypes.DRY.getSlickSpeedMultiplier();;
        }
        return doubles;
    }

    private static TimerTask getWeatherTask(Race race, WeatherTransitionSpeed transitionSpeed, WeatherTypes type) {
        double addedAmount = transitionSpeed.getExtraTransitionTicks();
        int goal = type.getWaterPercentage();
        return new TimerTask() {
            @Override
            public void run() {
                double waterPercentage = race.getRaceLapStorage().getWaterPercentage();
                if(waterPercentage > type.getWaterPercentage()) {
                    double value = Math.max(waterPercentage - addedAmount, 0);
                    race.getRaceLapStorage().setWaterPercentage(Math.max(value, goal));
                    Bukkit.getLogger().warning(String.valueOf(waterPercentage - addedAmount));
                } else {
                    double value = Math.min(waterPercentage + addedAmount, 100);
                    race.getRaceLapStorage().setWaterPercentage(Math.min(value, goal));
                    Bukkit.getLogger().warning(String.valueOf(waterPercentage + addedAmount));
                }
                if(race.getRaceLapStorage().getWaterPercentage() == type.getWaterPercentage()) {
                    race.getRaceLapStorage().setWeatherType(type);
                    cancel();
                }
            }
        };
    }
}
