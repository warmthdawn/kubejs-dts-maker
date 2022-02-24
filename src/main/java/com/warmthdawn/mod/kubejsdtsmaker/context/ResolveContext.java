package com.warmthdawn.mod.kubejsdtsmaker.context;

import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolveContext {
    private Map<Class<?>, JavaTypeInfo> typeInfos;


    private final GlobalTypeScope typeScope;

    public ResolveContext() {
        typeScope = new GlobalTypeScope();
        typeInfos = new HashMap<>();
    }

    public GlobalTypeScope getTypeScope() {
        return typeScope;
    }

    public boolean isResolved(Class<?> clazz) {
        return typeInfos.containsKey(clazz);
    }

    public JavaTypeInfo get(Class<?> clazz) {
        return typeInfos.get(clazz);
    }

    public void add(Class<?> clazz, JavaTypeInfo info) {
        typeInfos.put(clazz, info);
    }

}
