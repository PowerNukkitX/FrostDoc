package cn.powernukkitx.fd.render.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class ArrayUtils {
    private ArrayUtils() {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true, value = "null, _ -> false; !null, null -> false")
    public static <T> boolean contains(T @Nullable [] array, T value) {
        if (array == null) return false;
        for (var o : array) {
            if (o.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
