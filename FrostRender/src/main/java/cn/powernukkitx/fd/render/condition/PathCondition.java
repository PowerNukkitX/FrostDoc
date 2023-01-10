package cn.powernukkitx.fd.render.condition;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

@ThreadSafe
public class PathCondition implements Condition {
    @NotNull
    private final Pattern pathPattern;

    public PathCondition(@NotNull Pattern pathPattern) {
        this.pathPattern = pathPattern;
    }

    public PathCondition(String pathPattern) {
        this.pathPattern = Pattern.compile(pathPattern);
    }

    @Override
    public boolean match(Renderer renderer, Page page) {
        return pathPattern.matcher(page.getPath().toString()).matches();
    }

    @NotNull
    public Pattern getPathPattern() {
        return pathPattern;
    }
}
