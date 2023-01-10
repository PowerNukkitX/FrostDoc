package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.StringUtils;
import com.google.gson.JsonObject;
import net.ifok.png.compress.PngEncoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@ThreadSafe
public class CompressImageStep implements Step {
    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        if (page.getRenderedContent() == null) {
            Logger.of(CompressImageStep.class).warn("need-content", page.getPath());
            return;
        }
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(page.getRenderedContent()));
        } catch (IOException e) {
            Logger.of(CompressImageStep.class).warn("failed-to-parse-image", page.getPath(), e.getLocalizedMessage());
            e.printStackTrace();
            return;
        }
        switch (StringUtils.afterFirst(page.getPath().getFileName().toString(), ".")) {
            case "png" -> {
                var encoder = new PngEncoder();
                encoder.setCompressed(true);
                try (var bos = new ByteArrayOutputStream(page.getRenderedContent().length >> 1)) {
                    encoder.write(bufferedImage, bos);
                    var bytes = bos.toByteArray();
                    if (bytes.length < page.getRenderedContent().length)
                        page.setRenderedContent(bytes);
                } catch (IOException e) {
                    Logger.of(CompressImageStep.class).warn("failed-to-parse-image", page.getPath(), e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
            case "jpg", "jpeg", "bmp", "gif" -> {
                var writer = ImageIO.getImageWritersByFormatName(StringUtils.afterFirst(page.getPath().getFileName().toString(), ".")).next();
                var bos = new ByteArrayOutputStream(1024);
                ImageOutputStream ios;
                try {
                    ios = ImageIO.createImageOutputStream(bos);
                } catch (IOException e) {
                    Logger.of(CompressImageStep.class).warn("failed-to-parse-image", page.getPath(), e.getLocalizedMessage());
                    e.printStackTrace();
                    return;
                }
                writer.setOutput(ios);

                var q = 0.05f;
                if (args != null && args.has("quality")) {
                    q = args.get("quality").getAsFloat();
                }

                var param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(q);
                }

                try {
                    writer.write(null, new IIOImage(bufferedImage, null, null), param);
                } catch (IOException e) {
                    Logger.of(CompressImageStep.class).warn("failed-to-parse-image", page.getPath(), e.getLocalizedMessage());
                    e.printStackTrace();
                    return;
                }

                try {
                    var bytes = bos.toByteArray();
                    if (bytes.length < page.getRenderedContent().length)
                        page.setRenderedContent(bytes);
                    bos.close();
                    ios.close();
                    writer.dispose();
                } catch (IOException e) {
                    Logger.of(CompressImageStep.class).warn("failed-to-parse-image", page.getPath(), e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
            default -> Logger.of(CompressImageStep.class).warn("unsupported-image-format", page.getPath());
        }

    }

    @Override
    public @NotNull String getName() {
        return "compress-image";
    }
}
