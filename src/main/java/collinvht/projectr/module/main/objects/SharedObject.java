package collinvht.projectr.module.main.objects;

import lombok.Getter;

public class SharedObject<A, B> {
    @Getter
    public final A object1;
    @Getter
    public final B object2;

    public SharedObject(A object1, B object2) {
        this.object1 = object1;
        this.object2 = object2;
    }
}
