package collinvht.f1mc.module.racing.module.weather.manager;

import collinvht.f1mc.module.racing.manager.managers.RaceManager;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTransitionSpeed;
import collinvht.f1mc.module.racing.module.weather.obj.WeatherTypes;
import collinvht.f1mc.module.racing.object.race.Race;
import collinvht.f1mc.util.DefaultMessages;

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
        int percentage = race.getRaceLapStorage().getWaterPercentage();
        return prefix + "We expect it to be " + WeatherTypes.fromPercentageAprox(percentage) + " currently.";
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
        race.getRaceLapStorage().setWeatherType(type);
        if(transitionSpeed == WeatherTransitionSpeed.INSTANT) {
            race.getRaceLapStorage().setWaterPercentage(type.getWaterPercentage());
        } else {
            TimerTask timerTask = getWeatherTask(race, transitionSpeed, type);
            weatherTimer.schedule(timerTask, delay, 10);
        }
        return prefix + "Weather is being adjusted";
    }

    private static TimerTask getWeatherTask(Race race, WeatherTransitionSpeed transitionSpeed, WeatherTypes type) {
        int waterPercentage = race.getRaceLapStorage().getWaterPercentage();
        int addedAmount = transitionSpeed.getExtraTransitionTicks();
        int goal = type.getWaterPercentage();
        return new TimerTask() {
            @Override
            public void run() {
                if(waterPercentage > type.getWaterPercentage()) {
                    race.getRaceLapStorage().setWaterPercentage(Math.min(waterPercentage + addedAmount, goal));
                } else {
                    race.getRaceLapStorage().setWaterPercentage(Math.max(waterPercentage - addedAmount, goal));
                }
                if(race.getRaceLapStorage().getWaterPercentage() == goal) {
                    cancel();
                }
            }
        };
    }
}
