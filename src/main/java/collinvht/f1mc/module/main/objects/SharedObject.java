package collinvht.f1mc.module.main.objects;

import lombok.Getter;

@Getter
public class SharedObject<A, B> {
    public final A object1;
    public final B object2;

    public SharedObject(A object1, B object2) {
        this.object1 = object1;
        this.object2 = object2;
    }
}
