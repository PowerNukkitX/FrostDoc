package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.NullUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@ThreadSafe
public class MultiLangLinkCollector implements Step {
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (args == null || args.size() == 0) {
            Logger.of(MultiLangLinkCollector.class).warn("step-arg-required", getName());
            return;
        }
        try {
            NullUtils.ensureHas(Logger.of(MultiLangLinkCollector.class), args, "regexp", "languages");
        } catch (Exception ignored) {
            return;
        }
        var regexp = Shared.getCachedPattern(Logger.of(MultiLangLinkCollector.class), args.get("regexp").getAsString());
        if (regexp == null) {
            return;
        }
        var languages = args.get("languages").getAsJsonArray();
        var linkObj = new JsonObject();
        for (var lang : languages) {
            var langStr = lang.getAsString();
            var link = regexp.matcher(page.getOutputPath().toString()).replaceAll(langStr);
            if (link != null) {
                linkObj.addProperty(langStr, page.getOutputPath().getParent().relativize(Path.of(link)).toString().replace('\\', '/'));
            }
        }
        page.setPageData("multi-language-links", new Gson().toJson(linkObj));
    }

    @Override
    public @NotNull String getName() {
        return "collector-multi-lang-link";
    }
}
