# TemplateStep  

应用模板。  

## 语法  

```json5
{
  "id": "template",
  "args": {
    // 指定要使用的模板
    "template": {
      // 此正则表达式的第一个捕获组中的内容当作名称来选择模板
      "regexp": "([a-z]{2}-[a-z]{2})[/\\\\]",
      // 可选参数，在regexp获取的内容后追加内容
      "insert-after": ".html",
      // 可选参数，在regexp获取的内容前追加内容
      "insert-before": "",
      // 可选参数，替换正则表达式中的部分内容
      "replace": {
        // 用来匹配模板的正则表达式中第一个捕获组捕获到的内容中符合此表达式的部分将被替换
        "regexp": "xxx",
        "replacement": "xxx"
      }
    },
    // 用之前渲染的内容替换掉模板中的%md%
    "embed-content-placeholder": "md"
  }
}
```