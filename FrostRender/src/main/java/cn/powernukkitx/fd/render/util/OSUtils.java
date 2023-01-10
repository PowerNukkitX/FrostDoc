package cn.powernukkitx.fd.render.util;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class OSUtils {
    private OSUtils() {

    }

    public static EnumOS getOS() {
        var os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return EnumOS.WINDOWS;
        } else if (os.contains("mac") || os.contains("darwin")) {
            return EnumOS.MACOS;
        } else if (os.contains("nux")) {
            return EnumOS.LINUX;
        } else {
            return EnumOS.UNKNOWN;
        }
    }

    public static String getProgramPath() {
        var path = OSUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = java.net.URLDecoder.decode(path, StandardCharsets.UTF_8);
        if (getOS() == EnumOS.WINDOWS) {
            path = path.substring(1);
        }
        return path;
    }

    @NotNull
    public static String getProgramDir() {
        var tmp = Path.of(getProgramPath());
        return tmp.getParent().toString();
    }

    @NotNull
    public static String getProgramName() {
        var tmp = Path.of(getProgramPath());
        var name = tmp.getFileName().toString();
        if (name.contains(".")) {
            return StringUtils.beforeLast(name, ".");
        }
        return name;
    }

}
