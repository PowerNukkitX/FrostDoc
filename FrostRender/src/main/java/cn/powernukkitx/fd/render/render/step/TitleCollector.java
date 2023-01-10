package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.NullUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

@ThreadSafe
public class TitleCollector implements Step {
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (page.getRenderedContent() == null) {
            Logger.of(TitleCollector.class).warn("need-content", page.getPath(), getName());
            return;
        }
        if (args == null || args.size() == 0) {
            Logger.of(TitleCollector.class).warn("step-arg-required", getName());
            return;
        }
        try {
            NullUtils.ensureHas(Logger.of(TitleCollector.class), args, "title-regexp");
        } catch (Exception e) {
            Logger.of(TitleCollector.class).warn("step-arg-required", getName());
            return;
        }
        var titleRegexp = args.get("title-regexp").getAsString();
        var pattern = Shared.getCachedPattern(Logger.of(TitleCollector.class), titleRegexp);
        if (pattern == null) return;
        var matcher = pattern.matcher(new String(page.getRenderedContent(), StandardCharsets.UTF_8));
        String title = null;
        if (matcher.find()) {
            title = matcher.group(1);
        }
        if (title == null) {
            Logger.of(TitleCollector.class).warn("title-not-found", page.getPath());
        } else {
            page.setPageData("title", title);
        }
    }

    @Override
    public @NotNull String getName() {
        return "collect-title";
    }
}
