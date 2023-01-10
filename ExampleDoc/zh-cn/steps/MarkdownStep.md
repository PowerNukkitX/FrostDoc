# MarkdownStep

将Markdown文件转换为Html代码。

## 语法

```json5
{
  "id": "markdown",
  "args": {
    // 可选参数，渲染出来的html元素的class属性
    "css-classes": {
      "h1": "mdui-text-color-theme",
      "h2": "mdui-text-color-theme",
      "h3": "mdui-text-color-theme",
      "table": "mdui-table mdui-table-hoverable"
    },
    // 可选参数，渲染模式
    // default / mdui-compatible
    "mode": "mdui-compatible" // 将会以mdui兼容的方式渲染
  }
}
```