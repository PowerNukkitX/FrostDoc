# MultiLangLinkCollector  

收集多语言链接，通常用于制作不同语言的文档，并允许自动跳转到用户使用的语言。 

多语言文档信息将被以JSON对象字符串的形式作为文档的`multi-language-links`属性存储，如果文档接下来有
`template-step`，则会将文档中的`%multi-language-links%`占位符替换掉。  

JSON对象字符串如下：  
```json
{
  "zh-cn": "link1.html",
  "en-us": "../en-us/link2.html"
}
```

## 语法  

```json5
{
  "id": "collector-multi-lang-link",
  "args": {
    // 每个文件的路径上符合正则表达式的部分将被替换为languages数组中的内容，然后进行排列组合
    "regexp": "([a-z]{2}-[a-z]{2})(?=[/\\\\])",
    // 最终信息将被收集为multi-language-links变量以供模板调用
    "languages": [
      "zh-cn",
      "en-us"
    ]
  }
}
```