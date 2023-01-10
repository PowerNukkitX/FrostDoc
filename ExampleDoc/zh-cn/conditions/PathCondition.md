# PathCondition  

全路径条件。  

如果文件的全路径匹配正则表达式则通过。  

## 语法  

```json
{
  "path": "正则表达式"
}
```

## 示例  

匹配`target`文件夹中的所有内容  
```json
{
  "path": "(.*target[/\\\\].*)"
}
```