package com.warmthdawn.mod.kubejsgrammardump.typescript;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsgrammardump.collector.GlobalCollector;
import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import com.warmthdawn.mod.kubejsgrammardump.collector.WrappedObjectCollector;
import com.warmthdawn.mod.kubejsgrammardump.extras.JavaMethodCallFix;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSUnionType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.*;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Variable;
import com.warmthdawn.mod.kubejsgrammardump.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DeclarationBuilder {
    private static final String INDENT = "    ";
    private static final Logger log = LogManager.getLogger();

    public static void build() {
        Utils.lazyTypes.clear();
        JavaClassCollector.INSTANCE.clear();
        GlobalCollector.INSTANCE.getGlobalVars().clear();
        GlobalCollector.INSTANCE.getTypeAliases().clear();
        WrappedObjectCollector.INSTANCE.clear();
        JavaMethodCallFix.INSTANCE.clear();
        try {
            JavaClassCollector.INSTANCE.findAllClasses();
        } catch (Exception e) {
            log.error("Failed to find all classes: ", e);
        }
        Utils.lazyTypes.forEach(LazyType::resolve);

        StringBuilder builder = new StringBuilder();

        GlobalCollector.INSTANCE.collectGlobals();
        for (TypeAlias typeAlias : GlobalCollector.INSTANCE.getTypeAliases()) {
            typeAlias.generate(builder);
            builder.append("\n\n");
        }
        for (Variable globalVar : GlobalCollector.INSTANCE.getGlobalVars()) {
            globalVar.generate(builder);
            builder.append("\n\n");
        }
        JavaClassCollector.INSTANCE.lock();
        Multimap<Namespace, IDeclaredType> classes = JavaClassCollector.INSTANCE.getClasses();
        for (Namespace namespace : classes.keySet()) {
            Collection<IDeclaredType> packageClasses = classes.get(namespace);
            Set<String> currentNames = new HashSet<>();
            for (IDeclaredType it : packageClasses) {
                if (it instanceof JavaClass || it instanceof TSUnionType) {
                    currentNames.add(it.getName());
                }
            }
            for (IDeclaredType it : packageClasses) {
                if (it instanceof JavaClassProto) {
                    ((JavaClassProto) it).adjustActualName(currentNames);
                    currentNames.add(((JavaClassProto) it).getActualName());
                }
            }
        }
        for (Namespace namespace : classes.keySet()) {
            namespace.generate(builder);
            builder.append(" {\n");
            Collection<IDeclaredType> packageClasses = classes.get(namespace);
            packageClasses.forEach(clazz -> {
                builder.append(INDENT);
                clazz.generate(builder);
                if (clazz instanceof AbstractClass) {
                    builder.append(" {\n");
                    ((AbstractClass) clazz).forEachMembers(it -> {
                        builder.append(INDENT).append(INDENT);
                        it.generate(builder);
                        builder.append("\n");
                    });
                    builder.append(INDENT).append("}\n");
                } else {
                    builder.append(";\n");
                }
            });
            builder.append("}\n\n");
        }
        JavaMethodCallFix.INSTANCE.generate(builder);

        String result = builder.toString();

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("minecraft.d.ts"), StandardCharsets.UTF_8)) {
            bufferedWriter.write(result);
            log.info("Export Successful");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
