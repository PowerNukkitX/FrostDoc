package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public class FetchFileStep implements Step {
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (page.getRenderedContent() != null) {
            Logger.of(FetchFileStep.class).warn("override-file", page.getPath());
        }
        page.setRenderedContent(page.getRawContent());
    }

    @Override
    public @NotNull String getName() {
        return "fetch-file";
    }
}
