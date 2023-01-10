package cn.powernukkitx.fd.render.util;

import cn.powernukkitx.fd.render.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PathCollector {
    private final List<Path> roots;
    private final List<Path> paths;
    private boolean changed = false;

    protected PathCollector() {
        this.paths = new ArrayList<>();
        this.roots = new ArrayList<>();
    }

    @NotNull
    public static PathCollector create(@NotNull String @NotNull ... rootPaths) {
        var pc = new PathCollector();
        for (var each : rootPaths) {
            pc.addRoot(Path.of(each));
        }
        return pc;
    }

    public void addRoot(Path path) {
        changed = true;
        roots.add(path);
    }

    public void addRoot(String path) {
        changed = true;
        roots.add(Path.of(path));
    }

    public List<Path> getPaths() {
        if (changed) {
            paths.clear();
            for (Path root : roots) {
                if (Files.isDirectory(root)) {
                    try (var stream = Files.walk(root)) {
                        stream.forEach(each -> {
                            if (Files.isRegularFile(each) && Files.isReadable(each)) {
                                paths.add(each);
                            }
                        });
                    } catch (IOException e) {
                        Logger.of(PathCollector.class).warn("failed-to-collect-files", root);
                    }
                } else if (Files.isReadable(root)) {
                    paths.add(root);
                }
            }
            changed = false;
        }
        return paths;
    }
}
