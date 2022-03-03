package com.warmthdawn.mod.kubejsdtsmaker.context;

import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.util.MiscUtils;

import java.util.HashMap;
import java.util.Map;

public class ResolveContext {
    private final Map<Class<?>, JavaTypeInfo> typeInfos;

    public Map<Class<?>, JavaTypeInfo> getTypeInfos() {
        return typeInfos;
    }

    private final GlobalTypeScope typeScope;
    private final ResolveBlacklist blacklist;

    public ResolveContext() {
        typeScope = new GlobalTypeScope();
        blacklist = new ResolveBlacklist();
        typeInfos = new HashMap<>();
        initPrimitive();
    }

    public GlobalTypeScope getTypeScope() {
        return typeScope;
    }

    public boolean isResolved(Class<?> clazz) {
        return typeInfos.containsKey(clazz);
    }

    public boolean canReference(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isPrimitive()) {
            return false;
        }
        if (wrappedObjects.containsKey(clazz)) {
            return false;
        }
        if (blacklist.isBlacklisted(clazz)) {
            return false;
        }
        return isResolved(clazz);
    }

    public JavaTypeInfo get(Class<?> clazz) {
        return typeInfos.get(clazz);
    }

    public void add(Class<?> clazz, JavaTypeInfo info) {
        typeInfos.put(clazz, info);
        typeScope.put(MiscUtils.getNamespace(clazz), clazz.getSimpleName());
    }

    public ResolveBlacklist getBlacklist() {
        return blacklist;
    }

    private final HashMap<Class<?>, TsType> wrappedObjects = new HashMap<>();

    public TsType findWrap(Class<?> clazz) {
        return wrappedObjects.get(clazz);
    }

    public void initPrimitive() {
        wrappedObjects.put(void.class, PredefinedType.VOID);
        wrappedObjects.put(String.class, PredefinedType.STRING);
        wrappedObjects.put(char.class, PredefinedType.VOID);
        wrappedObjects.put(Character.class, PredefinedType.STRING);
        wrappedObjects.put(boolean.class, PredefinedType.BOOLEAN);
        wrappedObjects.put(Boolean.class, PredefinedType.BOOLEAN);
        wrappedObjects.put(Number.class, PredefinedType.NUMBER);
        wrappedObjects.put(int.class, PredefinedType.NUMBER);
        wrappedObjects.put(Integer.class, PredefinedType.NUMBER);
        wrappedObjects.put(byte.class, PredefinedType.NUMBER);
        wrappedObjects.put(Byte.class, PredefinedType.NUMBER);
        wrappedObjects.put(short.class, PredefinedType.NUMBER);
        wrappedObjects.put(Short.class, PredefinedType.NUMBER);
        wrappedObjects.put(long.class, PredefinedType.NUMBER);
        wrappedObjects.put(Long.class, PredefinedType.NUMBER);
        wrappedObjects.put(double.class, PredefinedType.NUMBER);
        wrappedObjects.put(Double.class, PredefinedType.NUMBER);
        wrappedObjects.put(float.class, PredefinedType.NUMBER);
        wrappedObjects.put(Float.class, PredefinedType.NUMBER);
        wrappedObjects.put(Object.class, PredefinedType.ANY);
    }
}
