# RenameStep  

重命名文件。  

## 语法  

```json5
{
  "id": "rename-file",
  "args": {
    // 文件名中匹配此正则表达式的部分将被替换
    "regexp": "\\.md$",
    // 支持使用捕获组
    "replacement": ".html",
    // 可选参数，默认为false，是否重命名整个路径，默认只修改文件名
    "rename-path": false
  }
}
```