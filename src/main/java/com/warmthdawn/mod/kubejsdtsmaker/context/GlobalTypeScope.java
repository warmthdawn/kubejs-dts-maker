package com.warmthdawn.mod.kubejsdtsmaker.context;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalTypeScope {
    private Map<String, Set<String>> scope;


    public boolean put(String packageName, String clazzName) {
        return scope.computeIfAbsent(packageName, it -> new HashSet<>()).add(clazzName);
    }

    public String resolveNoConflict(String packageName, String clazzName) {
        String actualClazzName = clazzName;
        for (int i = 0; !put(packageName, actualClazzName); i++) {
            actualClazzName = clazzName + i;
        }
        return actualClazzName;
    }

}
