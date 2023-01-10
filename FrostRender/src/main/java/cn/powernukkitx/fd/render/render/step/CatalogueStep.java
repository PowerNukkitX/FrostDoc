package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Template;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.ConfigUtils;
import cn.powernukkitx.fd.render.util.NullUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.javascript.jscomp.jarjar.com.google.common.reflect.TypeToken;
import org.commonmark.renderer.html.HtmlWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@ThreadSafe
public class CatalogueStep implements Step {
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        var template = renderer.getTemplate(page.getPageData("template-name", ""));
        if (template == null) {
            Logger.of(CatalogueStep.class).warn("template-not-specify", page.getPath());
            return;
        }
        Map<String, String> cssClasses = args != null && args.has("css-classes") &&
                args.get("css-classes") instanceof JsonObject jsonObject ? ConfigUtils.GSON.get().fromJson(jsonObject,
                new TypeToken<Map<String, String>>() {
                }.getType()) : Map.of(
                "ol", "category-sub",
                "li", "category-item",
                "a", "category-link",
                "p", "category-title");
        var sb = new StringBuilder();
        var writer = new HtmlWriter(sb);
        renderCatalogue(writer, template, page, Objects.requireNonNull(template.getCatalogue()), cssClasses);
        var catalogueHTML = sb.toString();
        var embedContentPlaceholder = args != null && args.get("embed-content-placeholder") instanceof JsonPrimitive primitive && primitive.isString() ? primitive.getAsString() : "catalogue";
        page.setPageData(embedContentPlaceholder, catalogueHTML);
        if (page.getRenderedContent() != null){
            page.setRenderedContent(new String(page.getRenderedContent(), StandardCharsets.UTF_8).replace("%" + embedContentPlaceholder + "%", catalogueHTML));
        }
    }

    private void renderCatalogue(@NotNull HtmlWriter writer, @NotNull Template template, @NotNull Page page, @NotNull JsonObject catalogue, @NotNull Map<String, String> cssClasses) {
        writer.tag("ol", Map.of("class", cssClasses.getOrDefault("ol", "")));
        for (var each : catalogue.entrySet()) {
            var key = each.getKey();
            var value = each.getValue();
            if (value instanceof JsonPrimitive primitive && primitive.isString()) {
                writer.tag("li", Map.of("class", cssClasses.getOrDefault("li", "")));
                var catalogueRootPath = NullUtils.merge(template.getCataloguePath(), template.getPath(), Shared.WORKING_DIR.get()).getParent();
                var pagePath = catalogueRootPath.resolve(primitive.getAsString());
                var linkPath = page.getOutputPath().getParent().relativize(pagePath).toString().replace('\\', '/');
                writer.tag("a", Map.of("class", cssClasses.getOrDefault("a", ""), "href", linkPath));
                writer.text(key);
                writer.tag("/a");
                writer.tag("/li");
            } else if (value instanceof JsonObject jsonObject) {
                writer.tag("li", Map.of("class", cssClasses.getOrDefault("li", "")));
                writer.tag("p", Map.of("class", cssClasses.getOrDefault("p", "")));
                writer.text(key);
                writer.tag("/p");
                writer.tag("/li");
                renderCatalogue(writer, template, page, jsonObject, cssClasses);
            }
        }
        writer.tag("/ol");
    }

    @Override
    public @NotNull String getName() {
        return "catalogue";
    }
}
