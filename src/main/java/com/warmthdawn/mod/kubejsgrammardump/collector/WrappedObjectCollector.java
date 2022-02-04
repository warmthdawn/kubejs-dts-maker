package com.warmthdawn.mod.kubejsgrammardump.collector;

import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSPrimitive;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IDeclaredType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.TypeAlias;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class WrappedObjectCollector {
    public static WrappedObjectCollector INSTANCE = new WrappedObjectCollector();

    private HashMap<String, IType> wrappedObjects = new HashMap<>();
    private HashMap<String, TypeAlias> aliases = new HashMap<>();
    private HashMap<Class<?>, IType> alternativeTypes = new HashMap<>();

    private WrappedObjectCollector() {
        initPrimitive();
    }

    public void clear() {
        aliases.clear();
        alternativeTypes.clear();
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
        wrappedObjects.put("java.lang.Object", TSPrimitive.ANY);
    }


    public void addAlias(TypeAlias alias) {
        IType targetType = alias.getTargetType();
        if (targetType instanceof IDeclaredType) {
            aliases.put(targetType.getSignature(), alias);
        }
    }


    public void addAlternative(Class<?> clazz, IType alternative) {
        if (wrappedObjects.containsKey(clazz.getCanonicalName())) {
            return;
        }
        alternativeTypes.put(clazz, alternative);
    }

    public IType findAlternative(Class<?> clazz) {
        return alternativeTypes.get(clazz);
    }

    public IType findJSWarp(Class<?> clazz) {
        return wrappedObjects.get(clazz.getCanonicalName());
    }
}
