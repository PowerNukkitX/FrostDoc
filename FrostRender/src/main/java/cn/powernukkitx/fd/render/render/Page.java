package cn.powernukkitx.fd.render.render;

import cn.powernukkitx.fd.render.util.LazyData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public final class Page {
    private final Path path;
    private Path outputPath = null;
    private final LazyData<byte[]> rawContent;
    private final Map<String, String> pageData;
    private byte @Nullable [] renderedContent = null;

    public Page(Path path, Supplier<byte[]> rawContentSupplier, Map<String, String> pageData) {
        this.path = path;
        this.rawContent = new LazyData<>(rawContentSupplier);
        this.pageData = pageData;
    }

    public Path getPath() {
        return path;
    }

    public Path getOutputPath() {
        return outputPath == null ? path : outputPath;
    }

    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    public byte[] getRawContent() {
        return rawContent.get();
    }

    public Map<String, String> getPageData() {
        return pageData;
    }

    public void setPageData(String key, String value) {
        pageData.put(key, value);
    }

    public String getPageData(String key) {
        return pageData.get(key);
    }

    public String getPageData(String key, String defaultValue) {
        return pageData.getOrDefault(key, defaultValue);
    }

    public void setRenderedContent(@NotNull String renderedContent) {
        this.renderedContent = renderedContent.getBytes(StandardCharsets.UTF_8);
    }

    public void setRenderedContent(byte[] renderedContent) {
        this.renderedContent = renderedContent;
    }

    public byte @Nullable [] getRenderedContent() {
        return renderedContent;
    }
}
