package cn.powernukkitx.fd.render.render.step.markdown;

import cn.powernukkitx.fd.render.util.AtomicLazyData;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ModeRegistry {
    private static final AtomicLazyData<ConcurrentHashMap<String, List<Function<HtmlNodeRendererContext, NodeRenderer>>>> MODE_RENDERERS = new AtomicLazyData<>(ConcurrentHashMap::new);

    private ModeRegistry() {
    }

    public static void register(@NotNull String mode, @NotNull List<@NotNull Function<@NotNull HtmlNodeRendererContext, @NotNull NodeRenderer>> renderers) {
        MODE_RENDERERS.get().put(mode, renderers);
    }

    @Nullable
    public static List<@NotNull Function<@NotNull HtmlNodeRendererContext, @NotNull NodeRenderer>> get(String mode) {
        if (MODE_RENDERERS.get().isEmpty()) {
            init();
        }
        return MODE_RENDERERS.get().get(mode);
    }

    private static void init() {
        register("mdui-compatible", List.of(
                TaskListItemHtmlNodeRenderer::new
        ));
        register("default", List.of(
                org.commonmark.ext.task.list.items.internal.TaskListItemHtmlNodeRenderer::new
        ));
    }
}
