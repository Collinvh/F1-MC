package collinvht.zenticracing.commands.team.object;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

public class Bestelling {

    @Getter private int softAmount;
    @Getter private int mediumAmount;
    @Getter private int hardAmount;
    @Getter private int interAmount;
    @Getter private int wetAmount;
    @Getter private int fuelStackAmount;


    public Bestelling(int softAmount, int mediumAmount, int hardAmount, int interAmount, int wetAmount, int fuelStackAmount) {
        this.softAmount = softAmount;
        this.mediumAmount = mediumAmount;
        this.hardAmount = hardAmount;
        this.interAmount = interAmount;
        this.wetAmount = wetAmount;
        this.fuelStackAmount = fuelStackAmount;
    }

    public void updateBestelling(int softAmount, int mediumAmount, int hardAmount, int interAmount, int wetAmount, int fuelStackAmount) {
        this.softAmount = softAmount;
        this.mediumAmount = mediumAmount;
        this.hardAmount = hardAmount;
        this.interAmount = interAmount;
        this.wetAmount = wetAmount;
        this.fuelStackAmount = fuelStackAmount;
    }


    public static JsonObject getArray(Bestelling bestelling) {
        JsonObject object = new JsonObject();
        object.addProperty("softAmount", bestelling.softAmount);
        object.addProperty("mediumAmount", bestelling.mediumAmount);
        object.addProperty("hardAmount", bestelling.hardAmount);
        object.addProperty("interAmount", bestelling.interAmount);
        object.addProperty("wetAmount", bestelling.wetAmount);
        object.addProperty("fuelStackAmount", bestelling.fuelStackAmount);

        return object;
    }

    public static Bestelling readBestelling(JsonObject object) {
        if(object != null) {
            int softAmount = object.get("softAmount").getAsInt();
            int mediumAmount = object.get("mediumAmount").getAsInt();
            int hardAmount = object.get("hardAmount").getAsInt();
            int interAmount = object.get("interAmount").getAsInt();
            int wetAmount = object.get("wetAmount").getAsInt();
            int fuelStackAmount = object.get("fuelStackAmount").getAsInt();

            return new Bestelling(softAmount, mediumAmount, hardAmount, interAmount, wetAmount, fuelStackAmount);
        } else {
            return null;
        }
    }

    public String getString() {
        return null;
    }
}
