package cn.powernukkitx.fd.render;

import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.util.LazyData;
import cn.powernukkitx.fd.render.util.OSUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Shared {
    @NotNull
    public static final String VERSION = "0.0.1";
    @NotNull
    public static final LazyData<Path> WORKING_DIR = new LazyData<>(() -> Path.of(System.getProperty("user.dir")));
    @NotNull
    public static final LazyData<Path> OUTPUT_DIR = new LazyData<>(() -> WORKING_DIR.get().getParent().resolve("output"));
    @NotNull
    public static final LazyData<Path> PROGRAM_DIR = new LazyData<>(() -> Path.of(OSUtils.getProgramDir()));
    @NotNull
    private static final ConcurrentHashMap<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();
    @NotNull
    private static final ConcurrentHashMap<? super Object, String> STRING_CACHE = new ConcurrentHashMap<>();

    private Shared() {
    }

    @Nullable
    public static Pattern getCachedPattern(Logger logger, String pattern) {
        try {
            return PATTERN_CACHE.computeIfAbsent(pattern, (p) -> {
                try {
                    return Pattern.compile(p);
                } catch (Exception e) {
                    logger.error("invalid-regexp", p, e.getLocalizedMessage());
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @NotNull
    public static <T> String getCachedString(@Nullable T key, @NotNull Function<@Nullable T, @NotNull String> provider) {
        return STRING_CACHE.computeIfAbsent(key, (Function) provider);
    }
}
