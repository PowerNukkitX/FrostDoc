# OrCondition

或然条件。

如果子条件中任意条件通过，则通过。

## 语法

```json5
{
  "or": [
    {
      // condition 1
    },
    {
      // condition 2
    }
    // ......
  ]
}
```

## 示例

匹配`a.txt`和`b.txt`文件

```json
{
  "or": [
    {
      "filename": "a.txt"
    },
    {
      "filename": "b.txt"
    }
  ]
}
```