package cn.powernukkitx.fd.render.util;

import cn.powernukkitx.fd.render.log.Logger;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class NullUtils {
    private NullUtils() {
    }

    public static <T> T merge(T a, T b) {
        return a == null ? b : a;
    }

    public static <T> T merge(T a, T b, T c) {
        return a == null ? (b == null ? c : b) : a;
    }

    public static <T> T merge(T a, T b, T c, T d) {
        return a == null ? (b == null ? (c == null ? d : c) : b) : a;
    }

    public static <T> T merge(T a, T b, T c, T d, T e) {
        return a == null ? (b == null ? (c == null ? (d == null ? e : d) : c) : b) : a;
    }

    @SafeVarargs
    public static <T> T merge(T a, T b, T c, T d, T e, T... ts) {
        T result = a == null ? (b == null ? (c == null ? (d == null ? e : d) : c) : b) : a;
        if (result == null) {
            for (T t : ts) {
                if (t != null) {
                    return t;
                }
            }
        }
        return result;
    }

    public static void ensureHas(Logger logger, JsonObject obj, String @NotNull ... keys) {
        var list = new ArrayList<String>(keys.length);
        for (var key : keys) {
            if (!obj.has(key)) {
                logger.error("field-not-found", key, ConfigUtils.GSON.get().toJson(obj));
                list.add(key);
            }
        }
        if (!list.isEmpty())
            throw new IllegalArgumentException("Missing key(s): " + String.join(", ", list));
    }
}
