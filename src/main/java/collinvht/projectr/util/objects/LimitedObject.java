package collinvht.projectr.util.objects;

import lombok.Getter;
import scala.Int;
import scala.math.Numeric;

public class LimitedObject<Z extends Number> {

    @Getter
    private Z value;
    @Getter
    private final Z bottomLimit;
    @Getter
    private final Z topLimit;

    public LimitedObject(Z bottomLimit, Z topLimit) {
        this.value = bottomLimit;
        this.bottomLimit = bottomLimit;
        this.topLimit = topLimit;
    }


    public void setValue(Z integer) {
        if((float) integer < (float)  bottomLimit) {
            integer = bottomLimit;
        } else if((float) integer > (float) topLimit) {
            integer = topLimit;
        }

        this.value = integer;
    }
}