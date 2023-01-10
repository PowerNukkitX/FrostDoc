package cn.powernukkitx.fd.render.util;

import cn.powernukkitx.fd.render.log.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public final class StringUtils {
    @NotNull
    public static String beforeLast(@NotNull String str, String splitter) {
        final int i = str.lastIndexOf(splitter);
        if (i == -1) return str;
        return str.substring(0, i);
    }

    @NotNull
    public static String beforeFirst(@NotNull String str, String splitter) {
        final int i = str.indexOf(splitter);
        if (i == -1) return str;
        return str.substring(0, i);
    }

    @NotNull
    public static String afterFirst(@NotNull String str, String splitter) {
        final int i = str.indexOf(splitter);
        if (i == -1) return str;
        return str.substring(i + 1);
    }

    public static String displayableBytes(long bytes) {
        if (bytes >= 1024 * 1024 * 2) {
            return String.format("%.2fMB", bytes / 1024.0 / 1024);
        } else if (bytes >= 1024 * 2) {
            return String.format("%.2fKB", bytes / 1024.0);
        } else {
            return String.format("%dB", bytes);
        }
    }

    public static String displayableFreq(long hz) {
        if (hz >= 1000000000) {
            return String.format("%.2fGHz", hz / 1000000000.0);
        } else if (hz >= 1000 * 1000) {
            return String.format("%.2fMHz", hz / 1000000.0);
        } else if (hz >= 1000) {
            return String.format("%.2fKHz", hz / 1000.0);
        } else {
            return String.format("%dHz", hz);
        }
    }

    public static int getPrintLength(@NotNull String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;

        }
        return length;
    }

    @NotNull
    public static String uriSuffix(@NotNull String s) {
        final int splashIndex = s.lastIndexOf("/");
        String last;
        if (splashIndex == -1) {
            last = s;
        } else {
            last = s.substring(splashIndex + 1);
        }
        final int dotIndex = last.indexOf('.');
        if (dotIndex == -1) {
            return "";
        } else {
            return last.substring(dotIndex + 1);
        }
    }

    @NotNull
    public static String uriSuffix(@NotNull URL url) {
        return uriSuffix(url.toString());
    }

    public static String stripTrailing(@NotNull String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return str.substring(0, i + 1);
            }
        }
        return str;
    }

    public static boolean notEmpty(String str) {
        return str != null && !str.isBlank();
    }

    @Contract(pure = true)
    public static @NotNull String tryWrapQuotation(@NotNull String str) {
        if (str.contains(" ")) {
            return "\"" + str + "\"";
        }
        return str;
    }

    @Contract(pure = true, value = "_ -> !null")
    public static String toString(@Nullable Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    @Contract(pure = true, value = "_, null, _, _ -> null; _, !null, _, _ -> !null")
    public static String modifyWithJsonOperator(@NotNull Logger logger, @Nullable String str, @NotNull JsonObject jsonObject, @Nullable String... exceptions) {
        if (str == null) return null;
        StringBuilder strBuilder = new StringBuilder(str);
        for (var entry : jsonObject.entrySet()) {
            if ("replace".equals(entry.getKey()) && entry.getValue() instanceof JsonObject replaceObject) {
                try {
                    NullUtils.ensureHas(logger, replaceObject, "regexp", "replacement");
                } catch (Exception e) {
                    continue;
                }
                strBuilder = new StringBuilder(strBuilder.toString().replaceAll(replaceObject.get("regexp").getAsString(), replaceObject.get("replacement").getAsString()));
            } else if ("insert-before".equals(entry.getKey()) && entry.getValue() instanceof JsonPrimitive primitive && primitive.isString()) {
                strBuilder.insert(0, primitive.getAsString());
            } else if ("insert-after".equals(entry.getKey()) && entry.getValue() instanceof JsonPrimitive primitive && primitive.isString()) {
                strBuilder.append(primitive.getAsString());
            } else if (!ArrayUtils.contains(exceptions, entry.getKey())) {
                logger.warn("invalid-str-operator", entry.getKey(), ConfigUtils.GSON.get().toJson(entry.getValue()));
            }
        }
        return strBuilder.toString();
    }
}