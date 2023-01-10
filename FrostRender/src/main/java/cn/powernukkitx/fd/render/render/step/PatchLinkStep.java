package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.NullUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@ThreadSafe
public class PatchLinkStep implements Step {
    public static final Pattern LINK_PATTERN = Pattern.compile("(?<=src=\")(.+?)(?=\")|(?<=href=\")(.+?)(?=\")|(?<=!?\\[.{0,16}]\\()(.+?)(?=\\))");

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (page.getRenderedContent() == null) {
            return;
        }
        var content = new String(page.getRenderedContent(), StandardCharsets.UTF_8);
        var template = renderer.getTemplate(page.getPageData("template-name", ""));
        var originalPath = template == null ? page.getPath() : NullUtils.merge(template.getPath(), page.getPath());
        var patched = LINK_PATTERN.matcher(content).replaceAll(matchResult -> {
            var link = matchResult.group();
            if (link.startsWith("#") || link.indexOf(':') > 1) return link;
            var folderRelativePath = page.getOutputPath().getParent().relativize(originalPath.getParent());
            return folderRelativePath.resolve(link).toString().replace('\\', '/');
        });
        page.setRenderedContent(patched);
    }

    @Override
    public @NotNull String getName() {
        return "patch-link";
    }
}
