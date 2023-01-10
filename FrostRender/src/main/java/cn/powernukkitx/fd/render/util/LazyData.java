package cn.powernukkitx.fd.render.util;

import java.util.function.Function;
import java.util.function.Supplier;

public final class LazyData<T> {
    private T data;
    private final Supplier<T> supplier;

    public LazyData(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public LazyData(Function<LazyData<T>, T> supplier) {
        this.supplier = () -> supplier.apply(this);
    }

    public T get() {
        if (data == null) {
            data = supplier.get();
        }
        return data;
    }

    public void set(T t) {
        data = t;
    }
}
