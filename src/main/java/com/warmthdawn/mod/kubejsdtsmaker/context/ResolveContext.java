package com.warmthdawn.mod.kubejsdtsmaker.context;

import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedTypes;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.util.MiscUtils;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolveContext {
    private final Map<Class<?>, JavaTypeInfo> typeInfos;

    public Map<Class<?>, JavaTypeInfo> getTypeInfos() {
        return typeInfos;
    }

    private final GlobalTypeScope typeScope;

    public ResolveContext() {
        typeScope = new GlobalTypeScope();
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
        if (clazz.isPrimitive()) {
            return false;
        }
        if (wrappedObjects.containsKey(clazz)) {
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


    private final HashMap<Class<?>, TsType> wrappedObjects = new HashMap<>();

    public TsType findWrap(Class<?> clazz) {
        return wrappedObjects.get(clazz);
    }

    public void initPrimitive() {
        wrappedObjects.put(void.class, PredefinedTypes.VOID);
        wrappedObjects.put(String.class, PredefinedTypes.STRING);
        wrappedObjects.put(char.class, PredefinedTypes.VOID);
        wrappedObjects.put(Character.class, PredefinedTypes.STRING);
        wrappedObjects.put(boolean.class, PredefinedTypes.BOOLEAN);
        wrappedObjects.put(Boolean.class, PredefinedTypes.BOOLEAN);
        wrappedObjects.put(Number.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(int.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(Integer.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(byte.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(Byte.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(short.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(Short.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(long.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(Long.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(double.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(Double.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(float.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(Float.class, PredefinedTypes.NUMBER);
        wrappedObjects.put(Object.class, PredefinedTypes.OBJECT);
    }
}
