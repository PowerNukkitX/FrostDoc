# CatalogueStep  

渲染目录并注入到文档中。  

## 语法  

```json5
{
  "id": "catalogue",
  "args": {
    // 可选参数，指定渲染后各个html元素的class属性
    "css-classes": {
      "ol": "category-sub another-class",
      "li": "category-item",
      "a": "category-link",
      "p": "category-title"
    },
    // 可选参数，指定注入的占位符，如果不指定则默认为%catalogue%
    "embed-content-placeholder": "c" // 此例子指定为%c%
  }
}
```