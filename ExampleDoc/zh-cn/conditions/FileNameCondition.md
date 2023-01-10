# FileNameCondition  

文件名条件。  

如果文件名符合正则表达式则通过。  

## 语法

```json5
{
  "filename": "正则表达式"
}
```

## 示例

匹配所有文件
```json
{
  "filename": ".*"
}
```

匹配图像文件
```json
{
  "filename": ".*\\.(png|jpg|jpeg|gif|bmp)$"
}
```

匹配除了`min.js`之外的所有`*.js`文件
```json
{
  "filename": ".*(?<!\\.min)\\.js$"
}
```