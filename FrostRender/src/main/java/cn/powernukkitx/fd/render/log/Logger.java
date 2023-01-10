package cn.powernukkitx.fd.render.log;

import cn.powernukkitx.fd.render.util.ConfigUtils;
import cn.powernukkitx.fd.render.util.LazyData;
import cn.powernukkitx.fd.render.util.StringUtils;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Logger {
    @NotNull
    public static final LazyData<Map<String, String>> languageMap = new LazyData<>(ConfigUtils::loadFrostRenderLanguageMap);
    private static final Map<String, Logger> loggerCache = new WeakHashMap<>();
    // e.g. %key.a.b% or %k1%
    private static final Pattern translateComponentPattern = Pattern.compile("%([a-zA-Z.0-9]+)%");
    // e.g. {} {} {} equals {1} {2} {3}
    private static final Pattern placeHolderComponentPattern = Pattern.compile("\\{([0-9]*)}");
    // HH:mm:ss
    private static final LazyData<DateTimeFormatter> timeFormatter = new LazyData<>(() -> DateTimeFormatter.ofPattern("HH:mm:ss"));

    private final String name;

    private Logger(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public static Logger of(String name) {
        return loggerCache.computeIfAbsent(name, Logger::new);
    }

    @NotNull
    public static Logger of(@NotNull Class<?> clazz) {
        return of(clazz.getSimpleName());
    }

    @Contract(pure = true)
    public static String translate(@NotNull Map<String, String> languageMap, String message) {
        // translate if the whole message is a key of language map
        message = languageMap.getOrDefault(message, message);
        // translate all components like %key% using regex
        if (message.indexOf('%') != -1) {
            message = translateComponentPattern.matcher(message).replaceAll(matchResult -> {
                var key = matchResult.group(1);
                return languageMap.getOrDefault(key, key);
            });
        }
        return message;
    }

    public static String format(@NotNull Map<String, String> languageMap, String message, Object... args) {
        message = translate(languageMap, message);
        // replace all placeholders like {} or {3} using regex
        // e.g. "{} {} {0}" + {"a", "b"} = "a b a"
        // e.g. "{2} {} {1} {}" + {"x", "y", "z"} = "z x y y"
        if (args != null && args.length != 0) {
            var defaultIndex = new AtomicInteger(0);
            message = placeHolderComponentPattern.matcher(message).replaceAll(matchResult -> {
                var index = matchResult.group(1);
                if (index == null) return "{undefined}";
                if (!index.isEmpty()) {
                    try {
                        var i = Integer.parseInt(index);
                        if (i >= args.length) return "{out_of_range}";
                        return Matcher.quoteReplacement(translate(languageMap, StringUtils.toString(args[i])));
                    } catch (NumberFormatException e) {
                        return "{invalid}";
                    }
                }
                var i = defaultIndex.getAndIncrement();
                if (i >= args.length) return "{out_of_range}";
                return Matcher.quoteReplacement(translate(languageMap, StringUtils.toString(args[i])));
            });
        }
        return message;
    }

    public void log(@NotNull String type, String message, Object... args) {
        message = format(languageMap.get(), message, args);
        var timeString = LocalDateTime.now().format(timeFormatter.get());
        var ansi = switch (type) {
            case "info" ->
                    new Ansi().fgBrightBlue().a(timeString).reset().a(" [").fgBlue().a("INFO").reset().a(" ] [").a(getName()).a("] ").reset();
            case "warn" ->
                    new Ansi().fgBrightBlue().a(timeString).reset().a(" [").fgYellow().a("WARN").reset().a(" ] [").a(getName()).a("] ").reset();
            case "error" ->
                    new Ansi().fgBrightBlue().a(timeString).reset().a(" [").fgRed().a("ERROR").reset().a("] [").a(getName()).a("] ").reset();
            default ->
                    new Ansi().fgBrightBlue().a(timeString).reset().a(" [").bold().a(type.toUpperCase()).reset().a("] [").a(getName()).a("] ").reset();
        };
        // print
        System.out.println(ansi.a(message));
    }

    public void info(String message, Object... args) {
        log("info", message, args);
    }

    public void warn(String message, Object... args) {
        log("warn", message, args);
    }

    public void error(String message, Object... args) {
        log("error", message, args);
    }
}
