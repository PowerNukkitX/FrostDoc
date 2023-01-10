package cn.powernukkitx.fd.render.condition;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.util.ConfigUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface Condition {
    boolean match(Renderer renderer, Page page);

    @Nullable
    static Condition fromJson(@NotNull JsonObject obj) {
        if (obj.has("filename")) {
            return new FileNameCondition(obj.get("filename").getAsString());
        } else if (obj.has("path")) {
            return new PathCondition(obj.get("path").getAsString());
        } else if (obj.has("or")) {
            var conditionsArray = obj.get("or").getAsJsonArray();
            var conditions = new Condition[conditionsArray.size()];
            for (int i = 0; i < conditionsArray.size(); i++) {
                conditions[i] = fromJson(conditionsArray.get(i).getAsJsonObject());
            }
            return new OrCondition(conditions);
        }
        Logger.of(Condition.class).warn("illegal-condition", ConfigUtils.GSON.get().toJson(obj));
        return null;
    }
}
