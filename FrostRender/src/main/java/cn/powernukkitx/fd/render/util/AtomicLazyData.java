package cn.powernukkitx.fd.render.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class AtomicLazyData<T> {
    private final AtomicReference<T> data = new AtomicReference<>();
    private final Supplier<T> supplier;

    public AtomicLazyData(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        return data.updateAndGet(t -> t == null ? supplier.get() : t);
    }

    public void set(T t) {
        data.set(t);
    }
}
