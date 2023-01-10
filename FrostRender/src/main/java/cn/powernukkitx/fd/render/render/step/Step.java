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

import java.util.HashMap;
import java.util.Map;

@ThreadSafe
public interface Step {
    void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args);

    @NotNull
    String getName();

    Map<String, Step> stepRegistry = new HashMap<>(8);

    static void registerStep(Step step) {
        stepRegistry.put(step.getName(), step);
    }

    static Step getStep(String name) {
        if (stepRegistry.isEmpty()) {
            registerDefaultSteps();
        }
        return stepRegistry.get(name);
    }

    static void registerDefaultSteps() {
        registerStep(new IgnoreStep());
        registerStep(new CompileJavaScriptStep());
        registerStep(new FetchFileStep());
        registerStep(new MoveDirStep());
        registerStep(new PatchLinkStep());
        registerStep(new MarkdownStep());
        registerStep(new RenameStep());
        registerStep(new TemplateStep());
        registerStep(new CatalogueStep());
        registerStep(new CompressImageStep());

        registerStep(new TitleCollector());
        registerStep(new MultiLangLinkCollector());
    }

    @Nullable
    static Step fromJson(@NotNull JsonObject obj) {
        NullUtils.ensureHas(Logger.of(Step.class), obj, "id");
        var name = obj.get("id").getAsString();
        var step = getStep(name);
        if (step == null) {
            Logger.of(Step.class).warn("step-not-found", name);
        }
        return step;
    }
}
