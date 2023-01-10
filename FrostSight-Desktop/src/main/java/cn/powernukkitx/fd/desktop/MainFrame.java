package cn.powernukkitx.fd.desktop;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.util.JsonUtils;
import co.casterlabs.rakurai.json.element.JsonString;
import dev.webview.Webview;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;

public final class MainFrame {
    public final Webview webview;

    public MainFrame() {
        webview = new Webview(true);
        var config = Desktop.CONFIG_OBJ.get();
        webview.setSize(JsonUtils.getInt(config, "size.width", 720),
                JsonUtils.getInt(config, "size.height", 720));
        webview.bind("echo", args -> {
            System.out.println(args);
            return null;
        });
        webview.bind("format", args -> {
            if (args == null || args.isEmpty()) return null;
            return new JsonString(Desktop.format(args.get(0).getAsString()));
        });
        webview.bind("getFrostSightVersion", args -> new JsonString(Desktop.VERSION));
        webview.bind("getFrostRendererVersion", args -> new JsonString(Shared.VERSION));
        webview.bind("selectDir", args -> {
            var dir = Desktop.DOC_DIR.get();
            if (dir == null) {
                dir = Desktop.APPDATA_PATH.get();
            }
            cn.powernukkitx.fd.desktop.Main.trySetTheme();
            var fc = new JFileChooser(dir.toFile());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle(Desktop.format("select-doc-folder"));
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                if (fc.getSelectedFile() != null && fc.getSelectedFile().isDirectory()) {
                    try (var stream = Files.walk(fc.getSelectedFile().toPath(), 1)) {
                        var result = stream.filter(p -> {
                            var fileName = p.getFileName().toString();
                            return fileName.contains(".json") && (fileName.contains("config") || fileName.contains("book"));
                        }).findFirst();
                        if (result.isPresent()) {
                            Desktop.DOC_DIR.set(fc.getSelectedFile().toPath());
                            FileWatcher.init();
                            return new JsonString("ok");
                        } else {
                            return new JsonString("error");
                        }
                    } catch (IOException e) {
                        return new JsonString("error");
                    }
                }
            }
            return new JsonString("none");
        });
        webview.setTitle("FrostSight");
        webview.dispatch(() -> webview.loadURL(Desktop.APPDATA_PATH.get().resolve("ui").resolve("index.html").toUri().toString().replace('\\', '/')));
        initTitleChanger();
        initRefresher();
    }

    public void run() {
        webview.run();
    }

    private void initTitleChanger() {
        Desktop.DOC_DIR.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                webview.setTitle("FrostSight");
            } else {
                webview.setTitle("FrostSight - " + newValue);
            }
        });
    }

    private void initRefresher() {
        Desktop.UPDATED_DOC_PAGES.addListener((observableValue, pages, t1) -> {
            for (var page : pages) {
                var relativePathStr = Desktop.DOC_DIR.get().relativize(page.getPath()).toString().replace("\\", "/");
                System.out.println(relativePathStr);
                webview.setTitle("FrostSight - " + relativePathStr);
                webview.dispatch(() -> {
                    webview.loadURL("file:///" + page.getOutputPath().toString().replace("\\", "/"));
                    webview.eval("""
                            window.addEventListener("load", function() {
                                document.querySelectorAll(".scrollable").forEach(a => {
                                    const data = Number(localStorage.getItem("scroll-state-" + a.id));
                                    if (data) {
                                        a.scrollTop = data;
                                    }
                                    a.addEventListener("scroll", e => {
                                        localStorage.setItem("scroll-state-" + e.path[0].id, "" + e.path[0].scrollTop);
                                    });
                                });
                            });
                            """);
                });
            }
            Desktop.UPDATED_DOC_PAGES.clear();
        });
    }
}
