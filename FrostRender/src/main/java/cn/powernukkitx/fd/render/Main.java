package cn.powernukkitx.fd.render;

import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Template;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.ConfigUtils;
import cn.powernukkitx.fd.render.util.NullUtils;
import cn.powernukkitx.fd.render.util.PathCollector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Main {
    // TODO: 2023/1/4 Rewrite with Apache Commons CLI
    public static void main(String[] args) {
        var start = System.currentTimeMillis();
        var pathCollector = PathCollector.create();
        // Parse args
        var state = State.NONE;
        for (int i = 0, len = args.length; i < len; i++) {
            var each = args[i];
            switch (each) {
                case "-wd" -> {
                    if (i + 1 < len) {
                        Shared.WORKING_DIR.set(Path.of(args[++i]));
                    } else {
                        Logger.of(Main.class).error("require-an-argument", "-wd");
                        return;
                    }
                }
                case "-od" -> {
                    if (i + 1 < len) {
                        Shared.OUTPUT_DIR.set(Path.of(args[++i]));
                    } else {
                        Logger.of(Main.class).error("require-an-argument", "-od");
                        return;
                    }
                }
                case "-i" -> state = State.ADDING_ROOT;
                case "-c" -> {
                    if (i + 1 < len) {
                        ConfigUtils.userConfigPath = Path.of(args[++i]);
                    } else {
                        Logger.of(Main.class).error("require-an-argument", "-c");
                        return;
                    }
                }
                default -> {
                    switch (state) {
                        case ADDING_ROOT -> pathCollector.addRoot(each);
                    }
                }
            }
        }
        // if there are no roots provided by user, use the working directory as the only root
        if (pathCollector.getPaths().isEmpty()) {
            pathCollector.addRoot(Shared.WORKING_DIR.get());
        }
        // Print dirs
        Logger.of(Main.class).info("working-on-dir", Shared.VERSION, Shared.WORKING_DIR.get());
        Logger.of(Main.class).info("output-into-dir", Shared.OUTPUT_DIR.get());
        // Render
        var result = render(pathCollector.getPaths());
        savePages(result);
        // Finish and print time.
        Logger.of(Main.class).info("finish-time", System.currentTimeMillis() - start);
    }

    enum State {
        NONE,
        ADDING_ROOT
    }

    public static List<Page> render(List<Path> paths) {
        var renderer = new Renderer(paths);
        {
            var templates = ConfigUtils.CONFIG_OBJ.get().getAsJsonArray("templates");
            for (var each : templates) {
                var obj = each.getAsJsonObject();
                NullUtils.ensureHas(Logger.of(Main.class), obj, "lang", "template-path", "catalogue-path");
                try {
                    var path = Shared.WORKING_DIR.get().resolve(Path.of(obj.get("template-path").getAsString()));
                    var cataloguePath = Shared.WORKING_DIR.get().resolve(Path.of(obj.get("catalogue-path").getAsString()));
                    renderer.removePath(path);
                    renderer.addTemplate(new Template(obj.get("lang").getAsString(), path, cataloguePath));
                    Logger.of(Main.class).info("template-added", path.getFileName().toString());
                } catch (IOException e) {
                    Logger.of(Main.class).error("cannot-read", "template-file", obj.get("template-path").getAsString(), e.getLocalizedMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        {
            var workflows = ConfigUtils.CONFIG_OBJ.get().getAsJsonArray("workflows");
            for (var each : workflows) {
                renderer.addWorkflow(Workflow.fromJson(each.getAsJsonObject()));
            }
        }
        return renderer.render();
    }

    private static void savePages(@NotNull List<Page> pages) {
        for (var page : pages) {
            var outputPath = Shared.OUTPUT_DIR.get().resolve(Shared.WORKING_DIR.get().relativize(page.getOutputPath()));
            try {
                if (Files.notExists(outputPath.getParent())) {
                    Files.createDirectories(outputPath.getParent());
                }
                // 有可能为空意味着被忽略
                if (page.getRenderedContent() != null)
                    Files.write(outputPath, page.getRenderedContent(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            } catch (IOException e) {
                Logger.of(Renderer.class).error("cannot-write", "content-file", outputPath, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
}