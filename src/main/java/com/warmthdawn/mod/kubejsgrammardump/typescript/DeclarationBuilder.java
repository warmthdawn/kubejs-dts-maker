package com.warmthdawn.mod.kubejsgrammardump.typescript;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsgrammardump.collector.GlobalCollector;
import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import com.warmthdawn.mod.kubejsgrammardump.collector.WrappedObjectCollector;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.*;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Variable;
import com.warmthdawn.mod.kubejsgrammardump.utils.Utils;

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

    public static void build() {
        Utils.lazyTypes.clear();
        JavaClassCollector.INSTANCE.getResolvedClasses().clear();
        JavaClassCollector.INSTANCE.getClasses().clear();
        GlobalCollector.INSTANCE.getGlobalVars().clear();
        GlobalCollector.INSTANCE.getTypeAliases().clear();
        WrappedObjectCollector.INSTANCE.clear();
        try {
            JavaClassCollector.INSTANCE.findAllClasses();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.lazyTypes.forEach(LazyType::resolve);

        StringBuilder builder = new StringBuilder();

        GlobalCollector.INSTANCE.collectGlobals();
        for (TypeAlias typeAlias : GlobalCollector.INSTANCE.getTypeAliases()) {
            WrappedObjectCollector.INSTANCE.addAlias(typeAlias);
        }
        for (TypeAlias typeAlias : GlobalCollector.INSTANCE.getTypeAliases()) {
            typeAlias.generate(builder);
            builder.append("\n\n");
        }
        for (Variable globalVar : GlobalCollector.INSTANCE.getGlobalVars()) {
            globalVar.generate(builder);
            builder.append("\n\n");
        }


        Multimap<Namespace, AbstractClass> classes = JavaClassCollector.INSTANCE.getClasses();
        for (Namespace namespace : classes.keySet()) {
            namespace.generate(builder);
            builder.append(" {\n");
            Collection<AbstractClass> packageClasses = classes.get(namespace);
            Set<String> currentNames = new HashSet<>();
            packageClasses.stream().filter(it -> it instanceof JavaClass).forEach(clazz -> {
                currentNames.add(clazz.getName());
            });
            packageClasses.stream().filter(it -> it instanceof JavaClassProto).forEach(clazz -> {
                ((JavaClassProto) clazz).adjustActualName(currentNames);
                currentNames.add(((JavaClassProto) clazz).getActualName());
            });
            packageClasses.forEach(clazz -> {
                builder.append(INDENT);
                clazz.generate(builder);
                builder.append(" {\n");
                clazz.forEachMembers(it -> {
                    builder.append(INDENT).append(INDENT);
                    it.generate(builder);
                    builder.append("\n");
                });
                builder.append(INDENT).append("}\n");
            });
            builder.append("}\n\n");
        }

        String result = builder.toString();

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("minecraft.d.ts"), StandardCharsets.UTF_8)) {
            bufferedWriter.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
