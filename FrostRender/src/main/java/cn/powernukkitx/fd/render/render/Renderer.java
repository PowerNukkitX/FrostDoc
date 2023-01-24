package cn.powernukkitx.fd.render.render;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Renderer {
    private final Map<String, Template> templates;
    private final List<Workflow> workflows;
    private final List<Path> paths;

    public Renderer(List<Path> paths) {
        this.paths = paths;
        this.templates = new HashMap<>(4);
        this.workflows = new ArrayList<>(8);
    }

    public void addTemplate(@NotNull Template template) {
        templates.put(template.getName(), template);
    }

    @Nullable
    public Template getTemplate(@NotNull String name) {
        return templates.get(name);
    }

    public void addWorkflow(@NotNull Workflow workflow) {
        workflows.add(workflow);
    }

    public void removePath(Path path) {
        paths.remove(path);
    }

    /**
     * @return 渲染完的页面对象
     */
    public List<Page> render() {
        return paths.parallelStream().map(path -> {
            var page = new Page(path, () -> {
                // read content
                try {
                    return Files.readAllBytes(path);
                } catch (IOException e) {
                    Logger.of(Renderer.class).error("cannot-read", "content-file", path, e.getLocalizedMessage());
                    return new byte[0];
                }
            }, new HashMap<>());
            var processed = false;
            for (var workflow : workflows) {
                if (workflow.match(this, page)) {
                    workflow.render(this, page);
                    processed = true;
                }
            }
            if (!processed) {
                Logger.of(Renderer.class).warn("file-no-workflow", path);
                page.setRenderedContent(page.getRawContent());
            }
            return page;
        }).collect(Collectors.toList());
    }
}
