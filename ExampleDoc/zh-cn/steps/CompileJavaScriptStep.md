# CompileJavaScriptStep  

编译并优化JavaScript文件。  

## 语法  

```json5
{
  "id": "compile-js",
  "args": {
    // 可选参数，源文件的语法等级
    "input-mode": "es?", // es6, es7, es8, es9, es10, es11, es12, esnext
    // 可选参数，编译后输出文件的语法等级
    "output-mode": "es?" // es5, es6, es7, es8, es9, es10, es11, es12
  }
}
```