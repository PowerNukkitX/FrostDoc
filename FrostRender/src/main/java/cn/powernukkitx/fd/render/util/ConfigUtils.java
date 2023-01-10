package cn.powernukkitx.fd.render.util;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.Main;
import cn.powernukkitx.fd.render.log.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class ConfigUtils {
    private ConfigUtils() {
    }

    @NotNull
    private static final LazyData<String> LANG = new LazyData<>(() -> {
        var lang = System.getProperty("user.language");
        var region = NullUtils.merge(System.getProperty("user.region"), System.getProperty("user.country"), System.getProperty("user.variant"));
        if (lang == null) {
            return "en-us";
        } else {
            return lang.toLowerCase() + "-" + (region == null ? "*" : region.toLowerCase());
        }
    });

    @NotNull
    public static final LazyData<Gson> GSON = new LazyData<>(() -> new GsonBuilder().setPrettyPrinting()
            .disableHtmlEscaping().create());

    @Nullable
    public static Path userConfigPath = null;

    @NotNull
    public static final LazyData<JsonObject> CONFIG_OBJ = new LazyData<>(() -> {
        try {
            if (userConfigPath == null) {
                try (var stream = Files.walk(Shared.WORKING_DIR.get(), 1)) {
                    stream.filter(p -> {
                        var fileName = p.getFileName().toString();
                        return fileName.contains(".json") && (fileName.contains("config") || fileName.contains("book"));
                    }).findFirst().ifPresent(value -> userConfigPath = value);
                }
            }
            if (userConfigPath == null) {
                Logger.of(ConfigUtils.class).error("file-not-found", "config-file");
                throw new RuntimeException("Config file not found");
            }
            var reader = new JsonReader(Files.newBufferedReader(Shared.WORKING_DIR.get().resolve(userConfigPath)));
            return GSON.get().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            Logger.of(ConfigUtils.class).error("cannot-read", "config-file", userConfigPath, e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    });

    public static Map<String, String> loadFrostRenderLanguageMap() {
        return loadLanguageMap("fr-language");
    }

    public static Map<String, String> loadLanguageMap(String folder) {
        var lang = LANG.get();
        var langInputStream = Main.class.getResourceAsStream("/" + folder + "/" + lang + ".json");
        if (langInputStream == null) {
            var commonLang = StringUtils.beforeLast(lang, "-");
            try (var languagesListInputStream = Main.class.getResourceAsStream("/" + folder + "/languages.json")) {
                if (languagesListInputStream != null) {
                    String[] languagesList = GSON.get().fromJson(new JsonReader(new InputStreamReader(languagesListInputStream)), String[].class);
                    for (var language : languagesList) {
                        if (language.startsWith(commonLang)) {
                            langInputStream = Main.class.getResourceAsStream("/" + folder + "/" + language + ".json");
                            break;
                        }
                    }
                }
            } catch (IOException ignored) {

            }
        }
        var enInputStream = lang.equals("en-us") ? langInputStream : Main.class.getResourceAsStream("/" + folder + "/en-us.json");
        try {
            if (enInputStream == null) {
                throw new RuntimeException("Failed to initialize language.");
            }
            Map<String, String> enLang = GSON.get().fromJson(new JsonReader(new InputStreamReader(enInputStream)), Map.class);
            if (langInputStream != null) {
                Map<String, String> userLang = GSON.get().fromJson(new JsonReader(new InputStreamReader(langInputStream)), Map.class);
                if (userLang != null) {
                    enLang.putAll(userLang);
                }
            }
            return enLang;
        } finally {
            if (langInputStream != null) {
                try {
                    langInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
