package collinvht.projectr.util.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

public class Setup {

    @Getter
    private final LimitedObject<Float> downForceLevel = new LimitedObject<>(1.6F, 2.0F);

    public double getDownForceFromSettings() {
        return downForceLevel.getValue();
    }

    public void setDownForceLevel(Float downForceLevel) {
        this.downForceLevel.setValue(downForceLevel);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("downForceLevel", downForceLevel.getValue());
        return object;
    }

    public static Setup fromJson(JsonObject setupInfo) {
        Setup setup = new Setup();
        setup.setDownForceLevel(setupInfo.get("downForceLevel").getAsFloat());
        return setup;
    }

}
