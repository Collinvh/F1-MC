package collinvht.projectr.commands.racing.setup.obj;

import lombok.Getter;

public class LimitedInteger {

    @Getter
    private int integer;
    @Getter
    private final int bottomLimit;
    @Getter
    private final int topLimit;

    public LimitedInteger(int bottomLimit, int topLimit) {
        this.integer = bottomLimit;
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
