package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.NullUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@ThreadSafe
public class RenameStep implements Step {
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (args == null || args.size() == 0) {
            Logger.of(RenameStep.class).warn("step-arg-required", getName());
            return;
        }
        try {
            NullUtils.ensureHas(Logger.of(RenameStep.class), args, "regexp", "replacement");
        } catch (IllegalArgumentException ignore) {
            return;
        }
        var regexp = args.get("regexp").getAsString();
        var replaceWith = args.get("replacement").getAsString();
        var renamePath = args.get("rename-path") instanceof JsonPrimitive primitive && primitive.isBoolean() && primitive.getAsBoolean();
        if (renamePath) {
            page.setOutputPath(Path.of(page.getOutputPath().toString().replaceAll(regexp, replaceWith)));
        } else {
            page.setOutputPath(Path.of(page.getOutputPath().getParent().toString(), page.getOutputPath().getFileName().toString().replaceAll(regexp, replaceWith)));
        }
    }

    @Override
    public @NotNull String getName() {
        return "rename-file";
    }
}
