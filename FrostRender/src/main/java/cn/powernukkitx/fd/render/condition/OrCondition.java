package cn.powernukkitx.fd.render.condition;

import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;

public class OrCondition implements Condition {
    private final Condition[] conditions;

    public OrCondition(Condition... conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean match(Renderer renderer, Page page) {
        for (var condition : conditions) {
            if (condition.match(renderer, page)) {
                return true;
            }
        }
        return false;
    }
}
