package collinvht.projectr.module.vehiclesplus.utils;

import collinvht.projectr.util.modules.ModuleBase;
import nl.sbdeveloper.vehiclesplus.api.VehiclesPlusAPI;
import nl.sbdeveloper.vehiclesplus.api.vehicles.HolderItemPosition;
import nl.sbdeveloper.vehiclesplus.api.vehicles.VehicleModel;
import nl.sbdeveloper.vehiclesplus.api.vehicles.impl.StorageVehicle;
import nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.seat.Seat;
import nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.skin.Skin;
import nl.sbdeveloper.vehiclesplus.api.vehicles.settings.UpgradableSetting;
import nl.sbdeveloper.vehiclesplus.api.vehicles.settings.impl.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static collinvht.projectr.util.Permissions.FIA_ADMIN;

public class VehicleInitializer extends ModuleBase {
    @Override
    public void load() {
        createF1Car("BaseF1", "BaseF1 Car", 10005);
    }

    @Override
    public void saveModule() {

    }


    public static void createF1Car(String vehicleName, String displayName, int data) {
        Sounds.SoundsBuilder soundsBuilder = Sounds.builder();
        soundsBuilder.idle(new Sounds.Sound("car.idle", 6));
        soundsBuilder.start(new Sounds.Sound("car.start", 2));
        soundsBuilder.accelerate(new Sounds.Sound("car.accelerate", 2));
        soundsBuilder.driving(new Sounds.Sound("car.driving", 2));
        soundsBuilder.slowingDown(new Sounds.Sound("car.slowingdown", 2));
        Sounds sounds = soundsBuilder.build();

        Permissions.PermissionsBuilder permissionsBuilder = Permissions.builder();
        permissionsBuilder.sitWithoutRidePermission(false);
        permissionsBuilder.ride("projectr.vehicle." + vehicleName.toLowerCase());
        permissionsBuilder.spawn(FIA_ADMIN.getPermission());
        permissionsBuilder.adjust(FIA_ADMIN.getPermission());
        permissionsBuilder.buy(FIA_ADMIN.getPermission());
        Permissions permissions = permissionsBuilder.build();

        VehicleModel.VehicleModelBuilder builder = VehicleModel.builder();
        builder.id(vehicleName);
        builder.displayName(displayName);
        builder.typeId("cars");
        builder.price(0);
        builder.permissions(permissions);
        builder.availableColor(Color.fromBGR(0,0,0));
        builder.part(new Skin(0,0,0, createStack(Material.NAME_TAG, data), HolderItemPosition.MAIN_HAND));
        builder.part(new Seat(0.785, -1.35, 0, 0, true));
        builder.maxSpeed(createSetting(240));
        builder.fuelTank(createSetting(100));
        builder.turningRadius(createSetting(7));
        builder.acceleration(createSetting(60));
        builder.hitbox(new Hitbox(3.05,2,0.8));
        builder.fuel(new Fuel("gasoline", 6));
        builder.exhaust(new Exhaust());
        builder.horn(new Horn());
        builder.sounds(sounds);
        builder.realisticSteering(false);
        builder.trunkSize(27);
        builder.drift(false);
        builder.exitWhileMoving(false);
        builder.health(100);

        VehicleModel model = builder.build();
        VehiclesPlusAPI.getVehicleModels().put(vehicleName, model);
        StorageVehicle vehicle = new StorageVehicle(model);
        vehicle.getStatics().setBrakeForceModifier(2.8F);
        VehiclesPlusAPI.addVehicle(vehicle, true);
    }

    public static ItemStack createStack(Material material, int customModelData) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if(meta == null) return stack;
        meta.setCustomModelData(customModelData);
        stack.setItemMeta(meta);
        return stack;
    }

    public static UpgradableSetting createSetting(int val) {
        return new UpgradableSetting(val, val, 0, 0);
    }
}
