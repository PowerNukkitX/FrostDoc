package cn.powernukkitx.fd.render.render.step;

import cn.powernukkitx.fd.render.api.ThreadSafe;
import cn.powernukkitx.fd.render.log.Logger;
import cn.powernukkitx.fd.render.render.Page;
import cn.powernukkitx.fd.render.render.Renderer;
import cn.powernukkitx.fd.render.render.Workflow;
import cn.powernukkitx.fd.render.util.AtomicLazyData;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ThreadSafe
public class CompileJavaScriptStep implements Step {
    public static final AtomicLazyData<List<SourceFile>> DEFAULT_EXTERN = new AtomicLazyData<>(() -> {
        try {
            return AbstractCommandLineRunner.getBuiltinExterns(CompilerOptions.Environment.BROWSER);
        } catch (IOException e) {
            Logger.of(CompileJavaScriptStep.class).warn("failed-to-launch-js-compiler", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    });

    @Override
    public void render(@NotNull Renderer renderer, @NotNull Workflow workflow, @NotNull Page page, @Nullable JsonObject args) {
        var outputStream = new ByteArrayOutputStream(32);
        var compiler = new Compiler(new PrintStream(outputStream));
        var options = new CompilerOptions();
        if (args != null && args.get("input-mode") instanceof JsonPrimitive primitive && primitive.isString()) {
            options.setLanguageIn(str2mode(primitive.getAsString(), CompilerOptions.LanguageMode.ECMASCRIPT_NEXT));
        }
        if (args != null && args.get("output-mode") instanceof JsonPrimitive primitive && primitive.isString()) {
            options.setLanguageOut(str2mode(primitive.getAsString(), CompilerOptions.LanguageMode.ECMASCRIPT_2018));
        }
        options.setOutputCharset(StandardCharsets.UTF_8);
        CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);
        var rawSource = page.getRenderedContent() == null ? page.getRawContent() : page.getRenderedContent();
        var source = SourceFile.fromCode(page.getPath().getFileName().toString(), new String(rawSource, StandardCharsets.UTF_8));
        var result = compiler.compile(DEFAULT_EXTERN.get(), List.of(source), options);
        if (result.success) {
            page.setRenderedContent(compiler.toSource());
        } else {
            page.setRenderedContent(rawSource);
        }
        if (outputStream.size() != 0) {
            Logger.of(CompileJavaScriptStep.class).warn("problem-when-compiling-js", page.getPath());
            for (var line : outputStream.toString().split("\n")) {
                if (line.isBlank()) continue;
                Logger.of(CompileJavaScriptStep.class).warn(line);
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "compile-js";
    }

    @NotNull
    private CompilerOptions.LanguageMode str2mode(@NotNull String str, @NotNull CompilerOptions.LanguageMode defaultMode) {
        return switch (str.toLowerCase()) {
            case "es3", "ecmascript3", "ecmascript_3" -> CompilerOptions.LanguageMode.ECMASCRIPT3;
            case "es5", "ecmascript5", "ecmascript_5" -> CompilerOptions.LanguageMode.ECMASCRIPT5;
            case "es6", "ecmascript6", "ecmascript_6", "es2015", "ecmascript2015", "ecmascript_2015" ->
                    CompilerOptions.LanguageMode.ECMASCRIPT_2015;
            case "es7", "ecmascript7", "ecmascript_7", "es2016", "ecmascript2016", "ecmascript_2016" ->
                    CompilerOptions.LanguageMode.ECMASCRIPT_2016;
            case "es8", "ecmascript8", "ecmascript_8", "es2017", "ecmascript2017", "ecmascript_2017" ->
                    CompilerOptions.LanguageMode.ECMASCRIPT_2017;
            case "es9", "ecmascript9", "ecmascript_9", "es2018", "ecmascript2018", "ecmascript_2018" ->
                    CompilerOptions.LanguageMode.ECMASCRIPT_2018;
            case "es10", "ecmascript10", "ecmascript_10", "es2019", "ecmascript2019", "ecmascript_2019" ->
                    CompilerOptions.LanguageMode.ECMASCRIPT_2019;
            case "es11", "ecmascript11", "ecmascript_11", "es2020", "ecmascript2020", "ecmascript_2020" ->
                    CompilerOptions.LanguageMode.ECMASCRIPT_2020;
            case "es12", "ecmascript12", "ecmascript_12", "es2021", "ecmascript2021", "ecmascript_2021" ->
                    CompilerOptions.LanguageMode.ECMASCRIPT_2021;
            case "es_next", "esnext", "ecmascript_next" -> CompilerOptions.LanguageMode.ECMASCRIPT_NEXT;
            default -> {
                Logger.of(CompileJavaScriptStep.class).warn("unknown-js-mode", str, defaultMode.name());
                yield CompilerOptions.LanguageMode.ECMASCRIPT_NEXT;
            }
        };
    }
}
