package cn.powernukkitx.fd.render.render;

import cn.powernukkitx.fd.render.condition.Condition;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.step.Step;
import cn.powernukkitx.fd.render.util.ConfigUtils;
import cn.powernukkitx.fd.render.util.NullUtils;
import cn.powernukkitx.fd.render.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Workflow {
    private final List<Condition> conditions;
    private final List<Pair<Step, JsonObject>> steps;

    public Workflow(List<Condition> conditions, List<Pair<Step, JsonObject>> steps) {
        this.conditions = conditions;
        this.steps = steps;
    }

    public boolean match(Renderer renderer, Page page) {
        for (Condition condition : conditions) {
            if (!condition.match(renderer, page)) {
                return false;
            }
        }
        return true;
    }

    public void render(Renderer renderer, Page page) {
        for (var entry : steps) {
            entry.a().render(renderer, this, page, entry.b());
        }
    }

    @Contract("_ -> new")
    public static @NotNull Workflow fromJson(@NotNull JsonObject obj) {
        NullUtils.ensureHas(Logger.of(Workflow.class), obj, "conditions", "steps");
        var conditionArr = obj.getAsJsonArray("conditions");
        var stepArr = obj.getAsJsonArray("steps");
        var conditionList = new ArrayList<Condition>(conditionArr.size());
        var stepList = new ArrayList<Pair<Step, JsonObject>>(stepArr.size());
        for (var each : conditionArr) {
            if (each instanceof JsonObject condition) {
                var cond = Condition.fromJson(condition);
                if (cond != null) {
                    conditionList.add(cond);
                }
            }
        }
        for (var each : stepArr) {
            if (each instanceof JsonObject step) {
                var stepObj = Step.fromJson(step);
                var argObj = step.has("args") ? step.getAsJsonObject("args") : null;
                if (stepObj != null) {
                    stepList.add(Pair.of(stepObj, argObj));
                }
            } else if (each instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString()) {
                var tmpObject = new JsonObject();
                tmpObject.addProperty("id", each.getAsString());
                stepList.add(Pair.of(Step.fromJson(tmpObject), null));
            } else if (each != null) {
                Logger.of(Workflow.class).warn("workflow-cannot-parse", ConfigUtils.GSON.get().toJson(each));
            }
        }
        return new Workflow(conditionList, stepList);
    }
}
