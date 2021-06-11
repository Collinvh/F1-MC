package collinvht.zenticracing.commands.racing.setup.obj;

import lombok.Getter;

public class LimitedFloat {

    @Getter
    private float aFloat;
    @Getter
    private final float bottomLimit;
    @Getter
    private final float topLimit;

    public LimitedFloat(float bottomLimit, float topLimit) {
        this.aFloat = bottomLimit;
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
