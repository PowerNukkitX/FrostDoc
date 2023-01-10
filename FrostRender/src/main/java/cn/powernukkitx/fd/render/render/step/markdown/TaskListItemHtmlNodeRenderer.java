package cn.powernukkitx.fd.render.render.step.markdown;

import org.commonmark.ext.task.list.items.TaskListItemMarker;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TaskListItemHtmlNodeRenderer implements NodeRenderer {
    private final HtmlNodeRendererContext context;
    private final HtmlWriter html;

    public TaskListItemHtmlNodeRenderer(@NotNull HtmlNodeRendererContext context) {
        this.context = context;
        this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Collections.singleton(TaskListItemMarker.class);
    }

    @Override
    public void render(Node node) {
        if (node instanceof TaskListItemMarker marker) {
            Map<String, String> attributes = new LinkedHashMap<>();
            attributes.put("type", "checkbox");
            attributes.put("disabled", "");
            if (marker.isChecked()) {
                attributes.put("checked", "");
            }
            this.html.tag("label", this.context.extendAttributes(node, "task-list-label", Map.of("style", "height:24px;padding:0 12px 0 12px;", "class", "mdui-checkbox")));
            this.html.tag("input", this.context.extendAttributes(node, "input-checkbox", attributes));
            this.html.tag("i", this.context.extendAttributes(node, "input-checkbox-icon", Map.of("class", "mdui-checkbox-icon")));
            this.html.tag("/i");
            this.html.tag("/label");
            this.html.text(" ");
            this.renderChildren(node);
        }
    }

    private void renderChildren(@NotNull Node parent) {
        Node next;
        for (Node node = parent.getFirstChild(); node != null; node = next) {
            next = node.getNext();
            this.context.render(node);
        }
    }
}
