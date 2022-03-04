package com.warmthdawn.mod.kubejsdtsmaker.special.template;

import com.warmthdawn.mod.kubejsdtsmaker.KubeJSDtsMaker;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.RawDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateUtils {
    public static List<String> replaceParameters(@NotNull BuildContext context, @NotNull Stream<String> lines, @NotNull Map<String, Class<?>> templateParameters) {
        List<String> result;
        if (!templateParameters.isEmpty()) {
            final String[] searchList = new String[templateParameters.size()];
            final String[] replacementList = new String[templateParameters.size()];
            ArrayList<Map.Entry<String, Class<?>>> entries = new ArrayList<>(templateParameters.entrySet());
            for (int i = 0; i < entries.size(); i++) {
                Map.Entry<String, Class<?>> entry = entries.get(i);
                String signature = BuilderUtils.createTypeReference(context, entry.getValue()).getSignature();
                searchList[i] = entry.getKey();
                replacementList[i] = signature;
            }
            result = lines.map(
                it -> StringUtils.replaceEach(it, searchList, replacementList)
            ).collect(Collectors.toList());
        } else {
            result = lines.collect(Collectors.toList());
        }
        return result;
    }




}
