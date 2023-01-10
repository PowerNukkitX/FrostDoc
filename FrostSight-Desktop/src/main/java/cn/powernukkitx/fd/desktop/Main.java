package cn.powernukkitx.fd.desktop;

import cn.powernukkitx.fd.render.util.JsonUtils;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Main {
    private static MainFrame mainFrame = null;

    public static void main(String[] args) {
        // set default global encoding = utf-8
        System.setProperty("file.encoding", "UTF-8");
        emitFile();
        FileWatcher.init();
        mainFrame = new MainFrame();
        loadConfig();
        mainFrame.run();
    }

    public static void trySetTheme() {
        // set look-and-feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void safeClose() {
        FileWatcher.close();
        collectUpConfig();
    }

    public static void loadConfig() {
        Desktop.DOC_DIR.set(JsonUtils.getPath(
                Desktop.CONFIG_OBJ.get(), "doc-dir", (Path) null
        ));
    }

    public static void emitFile() {
        try {
            Files.createDirectories(Desktop.APPDATA_PATH.get().resolve("ui"));
            Files.createDirectories(Desktop.APPDATA_PATH.get().resolve("image"));
            try (var s = Main.class.getResourceAsStream("/ui/index.html")) {
                Files.write(Desktop.APPDATA_PATH.get().resolve("ui").resolve("index.html"), Objects.requireNonNull(s).readAllBytes());
            }
            try (var s = Main.class.getResourceAsStream("/image/frost-gray.svg")) {
                Files.write(Desktop.APPDATA_PATH.get().resolve("image").resolve("frost-gray.svg"), Objects.requireNonNull(s).readAllBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void collectUpConfig() {
        var config = Desktop.CONFIG_OBJ.get();
        JsonUtils.setString(config, "doc-dir", Desktop.DOC_DIR.get().toString());
    }
}
