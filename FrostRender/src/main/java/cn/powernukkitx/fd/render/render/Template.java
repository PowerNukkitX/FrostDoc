package cn.powernukkitx.fd.render.render;

import cn.powernukkitx.fd.render.Shared;
import cn.powernukkitx.fd.render.util.ConfigUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static cn.powernukkitx.fd.render.render.step.PatchLinkStep.LINK_PATTERN;

public final class Template {
    private final String templateContent;
    private final String name;
    private final String lang;
    @Nullable
    private final Path path;
    @NotNull
    private final JsonObject catalogue;
    @Nullable
    final Path cataloguePath;

    public Template(String name, String lang, String templateContent, @NotNull JsonObject catalogue) {
        this.name = name;
        this.lang = lang;
        this.templateContent = templateContent;
        this.path = null;
        this.catalogue = catalogue;
        this.cataloguePath = null;
    }

    public Template(String lang, @NotNull Path path, @NotNull Path cataloguePath) throws IOException {
        this.lang = lang;
        this.name = path.getFileName().toString();
        this.path = path;
        this.templateContent = Files.readString(path, StandardCharsets.UTF_8);
        this.cataloguePath = cataloguePath;
        if (Files.isReadable(cataloguePath)) {
            this.catalogue = ConfigUtils.GSON.get().fromJson(Files.readString(Shared.WORKING_DIR.get().resolve(cataloguePath), StandardCharsets.UTF_8), JsonObject.class);
        } else {
            this.catalogue = new JsonObject();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public String render(@NotNull Page page, @NotNull Map<String, String> placeHolders) {
        var content = templateContent;
        // Patch links in templates
        if (path != null) {
            var originalPath = path;
            content = LINK_PATTERN.matcher(content).replaceAll(matchResult -> {
                var link = matchResult.group();
                if (link.startsWith("#") || link.indexOf(':') > 1) return link;
                var folderRelativePath = page.getOutputPath().getParent().relativize(originalPath.getParent());
                return folderRelativePath.resolve(link).toString().replace('\\', '/');
            });
        }
        // Replace placeholders
        for (var entry : placeHolders.entrySet()) {
            content = content.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return content;
    }

    public String getName() {
        return name;
    }

    public String getLang() {
        return lang;
    }

    @Nullable
    public Path getPath() {
        return path;
    }

    @NotNull
    public JsonObject getCatalogue() {
        return catalogue;
    }

    @Nullable
    public Path getCataloguePath() {
        return cataloguePath;
    }
}
