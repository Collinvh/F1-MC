package collinvht.f1mc.module.racing.module.weather.commands;

import collinvht.f1mc.module.racing.module.weather.commands.command.Forecast;
import collinvht.f1mc.module.racing.module.weather.commands.command.Weather;
import collinvht.f1mc.util.modules.CommandModuleBase;

public class WeatherCommands extends CommandModuleBase {
    @Override
    public void load() {
        registerCommand("weather", new Weather(), new Weather());
        registerCommand("forecast", new Forecast(), new Forecast());
    }
}
