# 设计与背景

## 背景

PowerNukkitX开发组长期使用自制的PowerDoc作为文档引擎，虽然它能满足我们的要求，但是距离好还差得远。  
它设计的时候完全没有考虑到可维护性，大量使用hack实现功能，而且没有任何注释，导致后期维护困难。~~（甚至代码里很多地方，
还被，明显地标记为“屎山”）~~。此外，很多步骤仍然需要手动完成，只能提交后查看结果，极大地拖累了文档编写的进度。

为了为项目编写良好的文档，我们决定重做PowerDoc，解决我们一年来遇到的种种问题。

## 设计

凛霜文档是对PowerDoc的完全重写，借鉴了PowerDoc的渲染流水线设计，但是在实现上，完全使用了新的技术栈。

- 每个文档都是完全可配置的，通过book文件可以自定义不同文件的渲染行为
- 通过自定义工作流，可以实现任意的渲染流水线
- 完整的CommonMark以及其他拓展支持
- 高性能并行渲染，充分利用每个核心，可以在数秒内渲染数百份文档
- 自带编辑器辅助，可以让JetBrains IDE、VsCode、Eclipse、Sublime等IDE中的更改实时呈现
- 内置JS优化器，可以让JS代码在编译时自动优化，减少体积
- 内置图片优化器，可以几乎无损压缩PNG、JPG、GIF等图片，大幅减少体积
- MDUI兼容模式，生成与MDUI匹配度更好的HTML

## MarkDown语法

### 任务列表

- Tasks:
- [x] Finished
- [ ] WIP

```markdown
- Tasks:
- [x] Finished
- [ ] WIP
```

### GitHub风格的表格

| Left-aligned | Center-aligned | Right-aligned |
|:-------------|:--------------:|--------------:|
| git status   |   git status   |    git status |
| git diff     |    git diff    |      git diff |

```markdown
| Left-aligned | Center-aligned | Right-aligned |
|:-------------|:--------------:|--------------:|
| git status   |   git status   |    git status |
| git diff     |    git diff    |      git diff |
```

### 删除线

~~Delete me~~

```markdown
~~Delete me~~
```

### 下划线

<u>Underline me</u>

```markdown
<u>Underline me</u>
```

### 图片属性

![PNX](../image/icon/PNX_LOGO_sm.png)  
![PNX](../image/icon/PNX_LOGO_sm.png){width=500 height=100}  
![PNX](../image/icon/PNX_LOGO_sm.png){width=100 height=100}

```markdown
![PNX](../image/icon/PNX_LOGO_sm.png)  
![PNX](../image/icon/PNX_LOGO_sm.png){width=500 height=100}  
![PNX](../image/icon/PNX_LOGO_sm.png){width=100 height=100}
```

### 标题链接

[标题链接](#标题链接)

```markdown
[标题链接](#标题链接)
```

## 文档配置

凛霜文档的文档配置文件是一个JSON文件，它可以通过流水线来配置文档的渲染行为。

每个配置文件都包括如下的部分：

- `book.json <object>`
    * `templates <array>` 文档所使用的模板
        + 中文正文模板
        + 中文主页模板
        + 英文正文模板
        + ......
    * `workflows <array>` 文档所使用的工作流
        + 配置文件工作流
        + 图片工作流
        + JS/CSS代码工作流
        + Markdown工作流
        + ......

### 模板

模板通常是一个html文件，也可以是其他文本文件，在渲染时其中的一些“占位符”，即形如`%xxxx`的字符串，会被替换为其他内容。

模板可以被工作流所应用到要处理的文件上，当然工作流也可以不应用任何模板。

模板通常包括如下部分：

- `template-path <string>` 模板文件相对于文档根目录的路径
- `catalogue-path <string>` 可选项，此模板所使用的[目录文件](#目录)的路径
- `lang <string>` 此模板所用的语言代码

这里给出一个样例模板配置：

```json5
{
  "template-path": "zh-cn.html",
  "catalogue-path": "zh-cn/catalogue.json",
  "lang": "zh-cn"
}
```

### 目录

目录文件是一个JSON文件，它包含了文档的目录结构。

`catalogue`步骤中将会将此文件渲染为html目录，并将其插入到模板的指定位置中。

目录文件是一个JSON对象，每个对象的键是在文档中显示出来的内容，而值就是文档相对于目录文件的相对路径。
如果想要表达层级结构，只需要将值设置为一个JSON对象，对象内容同上，请注意：**路径仍然是相对于目录JSON文件的相对路径**。

示例目录配置文件如下：

```json
{
  "凛霜文档": "FrostDoc.html",
  "设计与背景": "Design.html",
  "工作步骤": {
    "忽略文件": "steps/IgnoreStep.html"
  }
}
```

### 工作流

工作流是配置文件的最核心部分，规定了一份文档中不同类型的文件应该如何处理。

所有的文件都会根据工作流的触发条件被分配到不同的工作流上，如果一个文件不符合任何工作流的触发条件，
它将直接被原样复制到输出目录下。

工作流之间是可克隆并行处理的，凛霜文档引擎会自动根据计算机硬件情况和文件情况，按照工作流配置文件生成多个工作流实例，
这些工作流实例可能是相同的，也可能是不同的，一个工作流实例在渲染文档的时候只会处理一个文件，但是可能同时有多个
工作流实例在处理多份文件。

一个工作流通常包含包含如下部分：

- `root <object>` 工作流对象
    - `conditions <array>` 触发条件
        - `1 <object>` 第一个条件
        - `2 <object>` 第二个条件
        - ...... 所有条件都满足才会被触发
    - `steps <array>` 工作流步骤
        - `1 <object|string>` 第一个步骤（步骤若无参数可简写为其id字符串）
        - `2 <object|string>` 第二个步骤（步骤若无参数可简写为其id字符串）
        - ...... 所有步骤都会按顺序执行

这里给出本文的的工作流作为示例：

```json5
// 定义不同文件的处理方式
// Define different file processing methods
[
  // pom.xml，book.json这些配置文件直接忽略，我们不需要它们
  // pom.xml, book.json and other configuration files are ignored, we don't need them.
  {
    "conditions": [
      {
        "or": [
          {
            "filename": "(.*pom.xml)|(.*book.json5)|(.*FrostRender-{0,9}.*)|(catalogue.json)"
          },
          {
            "path": "(.*target[/\\\\].*)"
          }
        ]
      }
    ],
    "steps": [
      "ignore"
    ]
  },
  // js文件，除了min.js（已经编译完的文件）之外，都需要进行优化编译
  // js files, except min.js (already compiled files), need to be optimized and compiled
  {
    "conditions": [
      {
        // 正则表达式，匹配所有js文件，但是排除了min.js
        "filename": ".*(?<!\\.min)\\.js$"
      }
    ],
    "steps": [
      "fetch-file",
      {
        "id": "compile-js",
        "args": {
          "input-mode": "es12",
          // Android 6+
          "output-mode": "es7"
        }
      }
    ]
  },
  // markdown文件处理
  // markdown file processing
  {
    "conditions": [
      {
        "filename": ".*\\.md$"
      }
    ],
    "steps": [
      "fetch-file",
      // 从文件内容中摘取标题
      // Extract title from file content
      {
        "id": "collect-title",
        "args": {
          // 第一个group中的内容作为标题
          // The content of the first group is used as the title
          "title-regexp": "(?<!#)# (.+)(?= {0,2}[\\r\\n]{1,2})"
        }
      },
      // 修补markdown链接中错误的相对位置
      // Repair the wrong relative position in the Markdown link
      "patch-link",
      // 将markdown内容渲染为html
      // Render markdown content as html
      {
        "id": "markdown",
        "args": {
          "css-classes": {
            "h1": "mdui-text-color-theme",
            "h2": "mdui-text-color-theme",
            "h3": "mdui-text-color-theme",
            "table": "mdui-table mdui-table-hoverable"
          },
          // 将会以mdui兼容的方式渲染
          // Will be rendered in a mdui compatible way
          "mode": "mdui-compatible"
        }
      },
      // rename .md into .html
      {
        "id": "rename-file",
        "args": {
          "regexp": "\\.md$",
          "replacement": ".html",
          "rename-path": false
        }
      },
      // 收集多语言链接
      // Collect multi-language links
      {
        "id": "collector-multi-lang-link",
        "args": {
          // 每个文件的路径上符合正则表达式的部分将被替换为languages数组中的内容，然后进行排列组合
          // The part of the path of each file that matches the regular expression will be replaced with the content of the languages array, and then the permutation combination will be performed.
          "regexp": "([a-z]{2}-[a-z]{2})(?=[/\\\\])",
          // 最终信息将被收集为multi-language-links变量以供模板调用
          // The final information will be collected as the multi-language-links variable for template calls
          "languages": [
            "zh-cn",
            "en-us"
          ]
        }
      },
      // 将渲染出来的html应用到模板中
      // Apply rendered html to template
      {
        "id": "template",
        "args": {
          "template": {
            // 第一个group中的内容加上.html作为模板的名称
            // The content of the first group plus .html is used as the template name
            "regexp": "([a-z]{2}-[a-z]{2})[/\\\\]",
            "insert-after": ".html"
          },
          // 我们将用之前渲染的内容替换掉模板中的%md%
          // We will replace the %md% in the template with the previously rendered content
          "embed-content-placeholder": "md"
        }
      },
      // 添加目录到html中
      // Add a table of contents to html
      {
        "id": "catalogue",
        "args": {
          "css-classes": {
            "ol": "category-sub",
            "li": "category-item",
            "a": "category-link",
            "p": "category-title mdui-text-color-theme"
          },
          "embed-content-placeholder": "catalogue"
        }
      }
    ]
  },
  // 复制主页
  // Copy the index page
  {
    "conditions": [
      {
        "filename": ".*index\\.html$"
      }
    ],
    "steps": [
      "fetch-file",
      "patch-link"
    ]
  },
  // 压缩图片
  // Compress images
  {
    "conditions": [
      {
        "filename": ".*\\.(png|jpg|jpeg|gif|bmp)$"
      }
    ],
    "steps": [
      "fetch-file",
      {
        "id": "compress-image",
        "args": {
          // quality: (0, 1) 越小质量越低，体积越小
          // quality: (0, 1) The smaller the quality, the smaller the size
          "quality": 0.01
        }
      }
    ]
  }
]
```