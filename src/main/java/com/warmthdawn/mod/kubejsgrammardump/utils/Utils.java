package com.warmthdawn.mod.kubejsgrammardump.utils;

import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSArray;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSPrimitive;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.LazyType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.TypeAlias;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static IType getObjectType(Object o) {
        return getClassType(o.getClass());
    }

    public static List<LazyType> lazyTypes = new ArrayList<>();

    public static IType getClassType(Class<?> clazz) {
        if (clazz == null) {
            return TSPrimitive.UNKNOWN;
        }
        if (clazz == void.class) {
            return TSPrimitive.VOID;
        }
        if (clazz == String.class) {
            return TSPrimitive.STRING;
        }
        if (clazz == char.class) {
            return TSPrimitive.STRING;
        }
        if (clazz == byte.class ||
            clazz == short.class ||
            clazz == long.class ||
            clazz == int.class ||
            clazz == double.class ||
            clazz == float.class) {
            return TSPrimitive.NUMBER;
        }
        if (clazz == boolean.class) {
            return TSPrimitive.BOOLEAN;
        }
        LazyType lazyType = new LazyType(clazz);
        lazyTypes.add(lazyType);
        return lazyType;
    }


    public static Namespace getNamespace(String packageName) {
        return new Namespace(JSKeywords.convertPackageName(packageName));
    }
}
