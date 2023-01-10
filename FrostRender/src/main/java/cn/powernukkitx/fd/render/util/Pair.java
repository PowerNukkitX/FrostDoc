package cn.powernukkitx.fd.render.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Pair<A, B>(A a, B b) {
    @Contract("_, _ -> new")
    public static <A, B> @NotNull Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }
}
