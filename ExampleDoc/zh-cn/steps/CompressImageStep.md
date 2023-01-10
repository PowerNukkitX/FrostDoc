# CompressImageStep  

压缩图片。  

支持的格式：  

- png (优化率最好)
- jpg/jpeg
- bmp
- gif

## 语法  

```json5
{
  "id": "compress-image",
  "args": {
    // 可选参数，范围(0, 1)，越小质量越低，体积越小
    // 此参数对png无效，png格式通过超采样矢量化来压缩
    "quality": 0.01
  }
}
```