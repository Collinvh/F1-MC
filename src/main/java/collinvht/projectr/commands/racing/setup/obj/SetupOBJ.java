package collinvht.projectr.commands.racing.setup.obj;

import collinvht.projectr.commands.fia.Weer;
import collinvht.projectr.commands.racing.RaceManager;
import collinvht.projectr.commands.racing.computer.RaceCar;
import collinvht.projectr.commands.racing.object.RaceObject;
import collinvht.projectr.listener.VPPListener;
import collinvht.projectr.listener.vehicle.VehicleUtil;
import collinvht.projectr.manager.tyre.TyreData;
import collinvht.projectr.manager.tyre.TyreManager;
import collinvht.projectr.manager.tyre.Tyres;
import collinvht.projectr.util.objs.DiscordUtil;
import collinvht.projectr.util.objs.WeatherType;
import lombok.Getter;
import lombok.Setter;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.SpawnedVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.VehicleStats;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.UUID;

public class SetupOBJ {

    @Getter @Setter
    private TyreData prevTyre;

    @Getter
    private final UUID linkedPlayer;

    @Getter
    private final LimitedInteger frontWingAngle = new LimitedInteger(1, 11);
    @Getter
    private final LimitedInteger rearWingAngle = new LimitedInteger(1, 11);

    @Getter
    private final LimitedFloat frontCamber = new LimitedFloat(-3.5F, -2.5F);
    @Getter
    private final LimitedFloat rearCamber = new LimitedFloat(-2F, -1F);

    @Getter
    private final LimitedFloat frontToe = new LimitedFloat(0.05F,  0.15F);
    @Getter
    private final LimitedFloat rearToe = new LimitedFloat(0.2F, 0.50F);

    @Getter
    private final LimitedInteger frontRideHeight = new LimitedInteger(1, 11);
    @Getter
    private final LimitedInteger rearRideHeight = new LimitedInteger(1, 11);
    @Getter
    private final LimitedInteger brakePressure = new LimitedInteger(50, 100);
    @Getter
    private final LimitedInteger brakeBias = new LimitedInteger(50, 70);

    public SetupOBJ(UUID uuid) {
        linkedPlayer = uuid;
    }

    public void updateCar(Player player, SpawnedVehicle vehicle, RaceCar raceCar) {
        VehicleStats stats = vehicle.getStorageVehicle().getVehicleStats();
        VehicleUtil util = VPPListener.getUtil().get(vehicle.getStorageVehicle().getUuid());

        if(stats != null) {
            int baseSpeed = vehicle.getBaseVehicle().getSpeedSettings().getBase();
            int baseFuel = vehicle.getBaseVehicle().getFuelTankSettings().getBase();
            int baseAcceleration= vehicle.getBaseVehicle().getAccelerationSettings().getBase();
            int baseTurn = vehicle.getBaseVehicle().getTurningRadiusSettings().getBase();

            float curSteer = vehicle.getBaseVehicle().getTurningRadiusSettings().getBase();
            float curSteerInput = stats.getCurrentSteer();

            ItemStack stack = raceCar.getBandGui().getItem(13);
            if(stack != null) {
                TyreData tyre = TyreManager.getDataFromTyre(stack);

                if (tyre.getTyre() != Tyres.NULLTYRE && tyre.getDurability() > 0) {
                    if(prevTyre != null) {
                        if (prevTyre.getTyre() != tyre.getTyre()) {
                            if (RaceManager.getRunningRace() != null) {
                                RaceObject object = RaceManager.getRunningRace();
                                EmbedBuilder embedBuilder = new EmbedBuilder();
                                embedBuilder.setTitle("Banden Wissel", null);
                                embedBuilder.setColor(Color.GREEN);
                                embedBuilder.setDescription(player.getName());

                                embedBuilder.addField("Team", raceCar.getTeamObject().getTeamName(), false);
                                embedBuilder.addField("Vorige band", prevTyre.getTyre().getName(), false);
                                embedBuilder.addField("Nieuwe band", tyre.getTyre().getName(), false);

                                embedBuilder.setFooter("Project R | " + object.getRaceName());

                                DiscordUtil.getChannelByID(844159011666526208L).sendMessage(embedBuilder.build()).queue();
                                Bukkit.getLogger().warning("Message sent?");
                            } else {
                                Bukkit.getLogger().warning("RACE NULL");
                            }
                        }
                    }
                    prevTyre = tyre;


                    boolean isRaining = (Weer.getType() != WeatherType.OFF);
                    WeatherType type = Weer.getType();

                    baseSpeed += isRaining ? tyre.getTyre().getData().getWetspeed() : tyre.getTyre().getData().getExtraspeed();
                    curSteer *= tyre.getTyre().getData().getSteering();

                    if (isRaining) {
                        if (tyre.getTyre() != Tyres.WET || tyre.getTyre() != Tyres.INTER) {
                            baseTurn -= 1;
                            baseAcceleration -= 1;
                            baseSpeed -= type.getDry();
                        } else if(tyre.getTyre() == Tyres.WET) {
                            baseSpeed = type.getWet();
                        } else if(tyre.getTyre() == Tyres.INTER) {
                            baseSpeed = type.getInter();
                        }
                    } else {
                        if(tyre.getTyre() == Tyres.WET) {
                            baseTurn -= 1;
                            baseSpeed = type.getWet();
                        } else if(tyre.getTyre() == Tyres.INTER) {
                            baseTurn -= 1;
                            baseSpeed = type.getInter();
                        }
                    }

                    if (raceCar.getDriverObject() != null) {
                        if (raceCar.getDriverObject().isHasDRS() && raceCar.getDriverObject().isInDrsZone()) {
                            baseSpeed += 10;
                        }
                    }
                } else {
                    baseSpeed = 35;
                }
            } else {
                baseSpeed = 35;
            }

            if(util != null) {
                baseSpeed += util.getMaxSpeed();
                baseFuel += util.getFuelUsage();
            }

            Location location = player.getLocation();
            if(location.getWorld() != null) {
                Block block = location.getWorld().getBlockAt(location);
                switch (block.getType()) {
                    case GREEN_CONCRETE_POWDER:
                    case GRASS_BLOCK:
                        baseSpeed *= 0.8F;
                        break;
                    case ANDESITE:
                    case LIGHT_GRAY_CONCRETE_POWDER:
                    case LIGHT_GRAY_CONCRETE:
                    case COBBLESTONE:
                    case GRAVEL:
                        baseSpeed *= 0.5F;
                        break;
                    case TERRACOTTA:
                    case STONE:
                        baseSpeed *= 0.9F;
                        break;
                    case GRAY_CONCRETE_POWDER:
                        baseSpeed = 60;
                        break;
                }
            }


            stats.setSteering((int) curSteer);
            stats.setSpeed(baseSpeed);
            stats.setFuelTank(baseFuel);
            stats.setAcceleration(baseAcceleration);
            stats.setSteering(baseTurn);
        }
    }
}
