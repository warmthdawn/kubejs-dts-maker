package com.warmthdawn.mod.kubejsdtsmaker.context;

import com.warmthdawn.mod.kubejsdtsmaker.util.JSKeywords;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalTypeScope {
    private final Map<String, Set<String>> scope = new HashMap<>();


    public boolean put(String packageName, String clazzName) {
        return scope.computeIfAbsent(packageName, it -> new HashSet<>()).add(clazzName);
    }

    public String clazzNoConflict(String namespace, String clazzName) {
        String actualClazzName = clazzName;
        for (int i = 0; !put(namespace, actualClazzName); i++) {
            actualClazzName = clazzName + i;
        }
        return actualClazzName;
    }

    public String namespaceNoConflict(String namespace) {
        return "Packages." + JSKeywords.convertPackageName(namespace);
    }

}
