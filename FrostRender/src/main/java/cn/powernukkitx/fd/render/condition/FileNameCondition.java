package cn.powernukkitx.fd.render.condition;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

@ThreadSafe
public class FileNameCondition implements Condition {
    @NotNull
    private final Pattern fileNamePattern;

    public FileNameCondition(@NotNull Pattern fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    public FileNameCondition(String fileNamePattern) {
        this.fileNamePattern = Pattern.compile(fileNamePattern);
    }

    @Override
    public boolean match(Renderer renderer, Page page) {
        return fileNamePattern.matcher(page.getPath().getFileName().toString()).matches();
    }

    @NotNull
    public Pattern getFileNamePattern() {
        return fileNamePattern;
    }
}
