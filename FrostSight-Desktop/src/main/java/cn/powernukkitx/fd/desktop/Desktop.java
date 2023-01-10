package cn.powernukkitx.fd.desktop;

import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.parubok.swingfx.beans.property.*;
import io.github.parubok.swingfx.collections.FXCollections;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public final class Desktop {
    public static final String VERSION = "0.0.1";

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    @NotNull
    public static final LazyData<Map<String, String>> languageMap = new LazyData<>(() -> cn.powernukkitx.fd.render.util.ConfigUtils.loadLanguageMap("fsd-language"));

    public static final LazyData<Path> APPDATA_PATH = new LazyData<>(() -> {
        var os = OSUtils.getOS();
        return switch (os) {
            case WINDOWS:
                yield Path.of(System.getenv("APPDATA")).resolve("FrostSight");
            case MACOS:
                yield Path.of(System.getenv("HOME")).resolve("Library").resolve("Application Support").resolve("FrostSight");
            case LINUX:
                yield Path.of(System.getenv("HOME")).resolve(".config").resolve("FrostSight");
            default:
                throw new UnsupportedOperationException("Unsupported OS: " + os);
        };
    });

    public static final LazyData<JsonObject> CONFIG_OBJ = new LazyData<>(self -> {
        var tmp = new Thread(() -> {
            Main.safeClose();
            try {
                if (!Files.exists(APPDATA_PATH.get())) {
                    Files.createDirectories(APPDATA_PATH.get());
                }
                Files.writeString(APPDATA_PATH.get().resolve("config.json"), GSON.toJson(self.get()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tmp.setName("FrostSight-Desktop-Config-Save-Hook");
        tmp.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(tmp);
        if (Files.exists(APPDATA_PATH.get().resolve("config.json"))) {
            try {
                return GSON.fromJson(Files.readString(APPDATA_PATH.get().resolve("config.json")), JsonObject.class);
            } catch (IOException e) {
                e.printStackTrace();
                return new JsonObject();
            }
        } else {
            return new JsonObject();
        }
    });

    public static final BooleanProperty DARK_MODE = new SimpleBooleanProperty(false);

    public static final ObjectProperty<Path> DOC_DIR = new SimpleObjectProperty<>(null);

    public static final ListProperty<Page> UPDATED_DOC_PAGES = new SimpleListProperty<>(FXCollections.observableArrayList());

    public static FileWatcher FILE_WATCHER_THREAD = null;

    public static String format(@NotNull String key, Object... args) {
        return Logger.format(languageMap.get(), key, args);
    }
}
