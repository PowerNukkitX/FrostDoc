package cn.powernukkitx.fd.desktop;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.util.PathCollector;
import cn.powernukkitx.fd.render.util.StringUtils;
import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryWatcher;
import org.slf4j.helpers.NOPLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class FileWatcher {
    private static final AtomicReference<DirectoryWatcher> watchService = new AtomicReference<>(null);
    private static final Map<String, Long> lastRendered = new HashMap<>();

    public static void init() {
        Desktop.DOC_DIR.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            cn.powernukkitx.fd.render.Shared.WORKING_DIR.set(newValue);
            if (watchService.get() != null) {
                try {
                    watchService.get().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                watchService.set(DirectoryWatcher.builder()
                        .path(newValue)
                        .listener(event -> {
                            if (event.isDirectory() || event.eventType() == DirectoryChangeEvent.EventType.DELETE) {
                                return;
                            }
                            var path = Path.of(StringUtils.beforeFirst(event.path().toString(), "~"));
                            var lastRenderedTime = lastRendered.getOrDefault(path.toString(), 0L);
                            if (System.currentTimeMillis() - lastRenderedTime < 100) {
                                return;
                            }
                            lastRendered.put(path.toString(), System.currentTimeMillis());
                            if (Files.isRegularFile(path)) {
                                var pathCollector = PathCollector.create(path.toString().replace("~", ""));
                                var result = cn.powernukkitx.fd.render.Main.render(pathCollector.getPaths());
                                for (var page : result) {
                                    var outputPath = Shared.OUTPUT_DIR.get().resolve(Shared.WORKING_DIR.get().relativize(page.getOutputPath()));
                                    try {
                                        if (Files.notExists(outputPath.getParent())) {
                                            Files.createDirectories(outputPath.getParent());
                                        }
                                        // 有可能为空意味着被忽略
                                        if (page.getRenderedContent() != null) {
                                            Files.write(outputPath, page.getRenderedContent(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                                            if (outputPath.getFileName().toString().endsWith(".html")) {
                                                page.setOutputPath(outputPath);
                                                Desktop.UPDATED_DOC_PAGES.add(page);
                                            }
                                        }
                                    } catch (IOException e) {
                                        Logger.of(Renderer.class).error("cannot-write", "content-file", outputPath, e.getLocalizedMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        .fileHashing(true)
                        .logger(NOPLogger.NOP_LOGGER)
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            watchService.get().watchAsync();
        });
    }

    public static void close() {
        if (watchService.get() != null) {
            try {
                watchService.get().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
