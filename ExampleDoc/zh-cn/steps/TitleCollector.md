# TitleCollector  

从文档内容中提取此文档的标题。  

## 语法  

```json5
{
  "id": "collect-title",
  "args": {
    // 此正则表达式的第一个捕获组中的内容将作为标题。
    "title-regexp": "(?<!#)# (.+)(?= {0,2}[\\r\\n]{1,2})"
  }
}
```