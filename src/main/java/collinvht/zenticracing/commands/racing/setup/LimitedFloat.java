package collinvht.zenticracing.commands.racing.setup;

import lombok.Getter;

public class LimitedFloat {

    @Getter
    private float aFloat;
    private final float bottomLimit;
    private final float topLimit;

    public LimitedFloat(float bottomLimit, float topLimit) {
        this.bottomLimit = bottomLimit;
        this.topLimit = topLimit;
    }


    public void setValue(float aFloat) {
        if(aFloat < bottomLimit) {
            aFloat = bottomLimit;
        } else if(aFloat > topLimit) {
            aFloat = topLimit;
        }

        this.aFloat = aFloat;
    }
}
