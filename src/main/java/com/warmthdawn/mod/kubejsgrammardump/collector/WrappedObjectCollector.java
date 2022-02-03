package com.warmthdawn.mod.kubejsgrammardump.collector;

import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSPrimitive;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.TypeAlias;

import java.util.HashMap;

public class WrappedObjectCollector {
    public static WrappedObjectCollector INSTANCE = new WrappedObjectCollector();

    private HashMap<String, IType> wrappedObjects = new HashMap<>();
    private HashMap<String, TypeAlias> aliases = new HashMap<>();

    private WrappedObjectCollector() {
        initPrimitive();
    }

    public void clear() {
        aliases.clear();
    }

    public void initPrimitive() {
        wrappedObjects.put("java.lang.String", TSPrimitive.STRING);
        wrappedObjects.put("java.lang.Character", TSPrimitive.STRING);
        wrappedObjects.put("java.lang.Boolean", TSPrimitive.BOOLEAN);
        wrappedObjects.put("java.lang.Number", TSPrimitive.NUMBER);
        wrappedObjects.put("java.lang.Integer", TSPrimitive.NUMBER);
        wrappedObjects.put("java.lang.Byte", TSPrimitive.NUMBER);
        wrappedObjects.put("java.lang.Short", TSPrimitive.NUMBER);
        wrappedObjects.put("java.lang.Long", TSPrimitive.NUMBER);
        wrappedObjects.put("java.lang.Double", TSPrimitive.NUMBER);
        wrappedObjects.put("java.lang.Float", TSPrimitive.NUMBER);
    }


    public void addAlias(TypeAlias alias) {
        aliases.put(alias.getTargetType().getSignature(), alias);
    }


    public IType findJSWarp(Class<?> clazz, IType raw) {

        IType warp = wrappedObjects.get(clazz.getName());
        IType result = warp != null ? warp : raw;
        IType alias = aliases.get(result.getSignature());
        return alias != null ? alias : result;
    }
}
