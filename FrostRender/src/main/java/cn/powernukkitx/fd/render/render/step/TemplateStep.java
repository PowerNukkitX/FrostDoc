package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Template;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.NullUtils;
import cn.powernukkitx.fd.render.util.StringUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@ThreadSafe
public class TemplateStep implements Step {
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (args == null || args.size() == 0) {
            Logger.of(TemplateStep.class).warn("step-arg-required", getName());
            return;
        }
        try {
            NullUtils.ensureHas(Logger.of(TemplateStep.class), args, "template");
        } catch (Exception e) {
            return;
        }
        Template template = null;
        var templateJson = args.get("template");
        if (templateJson instanceof JsonPrimitive primitive && primitive.isString()) {
            template = renderer.getTemplate(primitive.getAsString());
        } else if (templateJson instanceof JsonObject jsonObject) {
            String templateName = null;
            if (jsonObject.get("name") instanceof JsonPrimitive namePrimitive && namePrimitive.isString()) {
                templateName = namePrimitive.getAsString();
            } else if (jsonObject.get("path") instanceof JsonPrimitive pathPrimitive && pathPrimitive.isString()) {
                templateName = pathPrimitive.getAsString();
            } else if (jsonObject.get("regexp") != null) {
                var regexpJson = jsonObject.get("regexp");
                // TODO: 2023/1/6 支持在内容中的正则表达式
                if (regexpJson instanceof JsonPrimitive regexpPrimitive && regexpPrimitive.isString()) {
                    var regexp = Shared.getCachedPattern(Logger.of(TemplateStep.class), regexpPrimitive.getAsString());
                    if (regexp == null) return;
                    var matcher = regexp.matcher(page.getOutputPath().toString());
                    if (matcher.find()) {
                        templateName = matcher.group(1);
                    }
                }
            }
            templateName = StringUtils.modifyWithJsonOperator(Logger.of(TemplateStep.class), templateName, jsonObject, "regexp", "name", "path");
            if (templateName != null) {
                template = renderer.getTemplate(templateName);
            }
        }
        if (template == null) {
            Logger.of(TemplateStep.class).warn("template-not-found", args.get("template").getAsString());
            return;
        }
        page.setPageData("template-name", template.getName());
        page.setPageData("lang", template.getLang());
        var embedContentPlaceholder = args.get("embed-content-placeholder") instanceof JsonPrimitive primitive && primitive.isString() ? primitive.getAsString() : "md";
        if (page.getRenderedContent() == null) {
            Logger.of(TemplateStep.class).warn("need-content", page.getPath(), getName());
            return;
        }
        var map = new HashMap<>(page.getPageData());
        map.put(embedContentPlaceholder, new String(page.getRenderedContent(), StandardCharsets.UTF_8));
        page.setRenderedContent(template.render(page, map));
    }

    @Override
    public @NotNull String getName() {
        return "template";
    }
}
