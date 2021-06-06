package collinvht.zenticracing.commands.racing.setup;

import lombok.Getter;

public class LimitedInteger {

    @Getter
    private int integer;
    private final int bottomLimit;
    private final int topLimit;

    public LimitedInteger(int bottomLimit, int topLimit) {
        this.bottomLimit = bottomLimit;
        this.topLimit = topLimit;
    }


    public void setValue(int integer) {
        if(integer < bottomLimit) {
            integer = bottomLimit;
        } else if(integer > topLimit) {
            integer = topLimit;
        }

        this.integer = integer;
    }
}
