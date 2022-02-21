package com.warmthdawn.mod.kubejsdtsmaker.context;

import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;

import java.util.Map;

public class ResolveContext {
    private Map<Class<?>, JavaTypeInfo> typeInfos;


    private final GlobalTypeScope typeScope;

    public ResolveContext() {
        typeScope = new GlobalTypeScope();
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
}
