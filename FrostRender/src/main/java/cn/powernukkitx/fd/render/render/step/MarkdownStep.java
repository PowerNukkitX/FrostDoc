package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.render.step.markdown.ModeRegistry;
import cn.powernukkitx.fd.render.util.AtomicLazyData;
import cn.powernukkitx.fd.render.util.ConfigUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.ext.task.list.items.internal.TaskListItemPostProcessor;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ThreadSafe
public class MarkdownStep implements Step {
    private static final AtomicLazyData<List<Extension>> EXTENSIONS = new AtomicLazyData<>(() -> List.of(
            StrikethroughExtension.create(),
            TablesExtension.create(),
            HeadingAnchorExtension.create(),
            ImageAttributesExtension.create()
    ));

    private static final AtomicLazyData<Parser> PARSER = new AtomicLazyData<>(() -> Parser.builder().extensions(EXTENSIONS.get())
            .postProcessor(new TaskListItemPostProcessor())
            .build());

    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (page.getRenderedContent() == null) {
            Logger.of(MarkdownStep.class).warn("need-content", page.getPath(), "markdown");
            return;
        }
        HtmlRenderer.Builder htmlRendererBuilder;
        var cssClass = args != null && args.has("css-classes") ? ConfigUtils.GSON.get().fromJson(args.get("css-classes"), Map.class) : null;
        if (cssClass == null) {
            htmlRendererBuilder = HtmlRenderer.builder().extensions(EXTENSIONS.get());
        } else {
            htmlRendererBuilder = HtmlRenderer.builder().extensions(EXTENSIONS.get())
                    .attributeProviderFactory(context -> (node, tagName, attributes) -> {
                        if (cssClass.containsKey(tagName))
                            attributes.put("class", cssClass.get(tagName).toString());
                    });
        }

        if (args != null && args.get("mode") instanceof JsonPrimitive primitive && primitive.isString()) {
            var modeList = ModeRegistry.get(primitive.getAsString());
            if (modeList != null) {
                modeList.forEach(rendererFactory -> htmlRendererBuilder.nodeRendererFactory(rendererFactory::apply));
            } else {
                Logger.of(MarkdownStep.class).warn("markdown-mode-not-found", primitive.getAsString());
            }
        } else {
            Objects.requireNonNull(ModeRegistry.get("default"))
                    .forEach(rendererFactory -> htmlRendererBuilder.nodeRendererFactory(rendererFactory::apply));
        }
        var rootNode = PARSER.get().parse(new String(page.getRenderedContent(), StandardCharsets.UTF_8));
        page.setRenderedContent(htmlRendererBuilder.build().render(rootNode));
    }

    @Override
    public @NotNull String getName() {
        return "markdown";
    }
}
