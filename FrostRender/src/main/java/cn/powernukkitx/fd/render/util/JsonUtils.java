package cn.powernukkitx.fd.render.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

@SuppressWarnings("DuplicatedCode")
public final class JsonUtils {
    private JsonUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    public static int getInt(@NotNull JsonObject jsonObject, @NotNull String key, int defaultValue) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                return defaultValue;
            }
        }
        var lastKey = keys[keys.length - 1];
        if (current.get(lastKey) instanceof JsonPrimitive primitive && primitive.isNumber()) {
            return primitive.getAsInt();
        } else {
            return defaultValue;
        }
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    public static int getInt(@NotNull JsonObject jsonObject, @NotNull String key, IntSupplier defaultValue) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                return defaultValue.getAsInt();
            }
        }
        var lastKey = keys[keys.length - 1];
        if (current.get(lastKey) instanceof JsonPrimitive primitive && primitive.isNumber()) {
            return primitive.getAsInt();
        } else {
            return defaultValue.getAsInt();
        }
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    public static boolean getBoolean(@NotNull JsonObject jsonObject, @NotNull String key, boolean defaultValue) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                return defaultValue;
            }
        }
        var lastKey = keys[keys.length - 1];
        if (current.get(lastKey) instanceof JsonPrimitive primitive && primitive.isBoolean()) {
            return primitive.getAsBoolean();
        } else {
            return defaultValue;
        }
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    public static boolean getBoolean(@NotNull JsonObject jsonObject, @NotNull String key, BooleanSupplier defaultValue) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                return defaultValue.getAsBoolean();
            }
        }
        var lastKey = keys[keys.length - 1];
        if (current.get(lastKey) instanceof JsonPrimitive primitive && primitive.isBoolean()) {
            return primitive.getAsBoolean();
        } else {
            return defaultValue.getAsBoolean();
        }
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    public static String getString(@NotNull JsonObject jsonObject, @NotNull String key, String defaultValue) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                return defaultValue;
            }
        }
        var lastKey = keys[keys.length - 1];
        if (current.get(lastKey) instanceof JsonPrimitive primitive && primitive.isString()) {
            return primitive.getAsString();
        } else {
            return defaultValue;
        }
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    @Nullable
    public static Path getPath(@NotNull JsonObject jsonObject, @NotNull String key, @Nullable Path defaultValue) {
        var pathStr = getString(jsonObject, key, (String) null);
        if (pathStr == null) return defaultValue;
        return Paths.get(pathStr);
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    @Nullable
    public static Path getPath(@NotNull JsonObject jsonObject, @NotNull String key, @NotNull Supplier<Path> defaultValue) {
        var pathStr = getString(jsonObject, key, (String) null);
        if (pathStr == null) return defaultValue.get();
        return Paths.get(pathStr);
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    public static String getString(@NotNull JsonObject jsonObject, @NotNull String key, Supplier<String> defaultValue) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                return defaultValue.get();
            }
        }
        var lastKey = keys[keys.length - 1];
        if (current.get(lastKey) instanceof JsonPrimitive primitive && primitive.isString()) {
            return primitive.getAsString();
        } else {
            return defaultValue.get();
        }
    }

    /**
     * Get a value from a JsonObject, if the value is not a JsonPrimitive, return defaultValue.
     *
     * @param jsonObject   The JsonObject
     * @param key          The key, like a.b.c.d.
     * @param defaultValue The default value
     * @return The value
     */
    public static double[] getDoubleArray(@NotNull JsonObject jsonObject, @NotNull String key, double[] defaultValue) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                return defaultValue;
            }
        }
        var lastKey = keys[keys.length - 1];
        if (current.get(lastKey) instanceof JsonArray array) {
            var result = new double[array.size()];
            for (int i = 0; i < array.size(); i++) {
                var element = array.get(i);
                if (element instanceof JsonPrimitive primitive && primitive.isNumber()) {
                    result[i] = primitive.getAsDouble();
                } else {
                    return defaultValue;
                }
            }
            return result;
        } else {
            return defaultValue;
        }
    }

    /**
     * Set a value of a JsonObject
     *
     * @param jsonObject The JsonObject
     * @param key        The key, like a.b.c.d.
     * @param value      The value
     */
    public static void setInt(@NotNull JsonObject jsonObject, @NotNull String key, int value) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                var next = new JsonObject();
                current.add(k, next);
                current = next;
            }
        }
        var lastKey = keys[keys.length - 1];
        current.addProperty(lastKey, value);
    }

    /**
     * Set a value of a JsonObject
     *
     * @param jsonObject The JsonObject
     * @param key        The key, like a.b.c.d.
     * @param value      The value
     */
    public static void setBoolean(@NotNull JsonObject jsonObject, @NotNull String key, boolean value) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                var next = new JsonObject();
                current.add(k, next);
                current = next;
            }
        }
        var lastKey = keys[keys.length - 1];
        current.addProperty(lastKey, value);
    }

    /**
     * Set a value of a JsonObject
     *
     * @param jsonObject The JsonObject
     * @param key        The key, like a.b.c.d.
     * @param value      The value
     */
    public static void setString(@NotNull JsonObject jsonObject, @NotNull String key, String value) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                var next = new JsonObject();
                current.add(k, next);
                current = next;
            }
        }
        var lastKey = keys[keys.length - 1];
        current.addProperty(lastKey, value);
    }

    /**
     * Set a value of a JsonObject
     *
     * @param jsonObject The JsonObject
     * @param key        The key, like a.b.c.d.
     * @param value      The value
     */
    public static void setDoubleArray(@NotNull JsonObject jsonObject, @NotNull String key, double[] value) {
        var keys = key.split("\\.");
        var current = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            var k = keys[i];
            if (current.get(k) instanceof JsonObject next) {
                current = next;
            } else {
                var next = new JsonObject();
                current.add(k, next);
                current = next;
            }
        }
        var lastKey = keys[keys.length - 1];
        var array = new JsonArray();
        for (var v : value) {
            array.add(v);
        }
        current.add(lastKey, array);
    }
}
