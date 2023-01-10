package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.NullUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@ThreadSafe
public class MoveDirStep implements Step {
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (args == null || args.size() == 0) {
            Logger.of(MoveDirStep.class).warn("step-arg-required", getName());
            return;
        }
        try {
            NullUtils.ensureHas(Logger.of(MoveDirStep.class), args, "move-into");
        } catch (IllegalArgumentException ignore) {
            return;
        }
        var moveIntoPath = Path.of(args.get("move-into").getAsString());
        var newPath = page.getOutputPath().getParent().resolve(moveIntoPath).resolve(page.getOutputPath().getFileName());
        page.setOutputPath(newPath);
    }

    @Override
    public @NotNull String getName() {
        return "move-dir";
    }
}
