package com.warmthdawn.mod.kubejsgrammardump.utils;

import com.warmthdawn.mod.kubejsdtsmaker.util.JSKeywords;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.ResolvedGenericType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSArray;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSPrimitive;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.LazyType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final Logger logger = LogManager.getLogger();

    public static IType getObjectTypeRaw(Object o) {
        return getClassTypeRaw(o.getClass());
    }

    public static List<LazyType> lazyTypes = new ArrayList<>();

    public static TSPrimitive getPrimitive(Class<?> clazz) {
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
        return null;
    }
    public static IType getClassTypeRaw(Class<?> clazz) {
        TSPrimitive primitive = getPrimitive(clazz);
        if (primitive != null) {
            return primitive;
        }
        return JavaResolveUtils.resolveClass(clazz);
    }

    public static IType getClassType(Class<?> clazz) {
        TSPrimitive primitive = getPrimitive(clazz);
        if (primitive != null) {
            return primitive;
        }
        LazyType lazyType = new LazyType(clazz);
        lazyTypes.add(lazyType);
        return lazyType;
    }


    public static IType getClassGenericType(Type type) {
        if (type instanceof Class) {
            return getClassType((Class<?>) type);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class) {
                IType rawResult = getClassType((Class<?>) rawType);
                Type[] arguments = parameterizedType.getActualTypeArguments();
                IType[] argumentTypes = new IType[arguments.length];
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i] instanceof TypeVariable) {
//                        logger.warn("Trying to resolve a variable generic: {}", type);
                        argumentTypes[i] = TSPrimitive.ANY;
                    } else {
                        IType argType = getClassGenericType(arguments[i]);
                        argumentTypes[i] = argType;
                    }
                }
                return new ResolvedGenericType(rawResult, argumentTypes);
            } else {
                return TSPrimitive.UNKNOWN;
            }
        }
        if (type instanceof GenericArrayType) {
            return new TSArray(getClassGenericType(((GenericArrayType) type).getGenericComponentType()));
        }
        if (type instanceof WildcardType) {
            return TSPrimitive.ANY;
        }
        return TSPrimitive.UNKNOWN;
    }

    public static Namespace getNamespace(String packageName) {
        return new Namespace(JSKeywords.convertPackageName(packageName));
    }

    public static Namespace getNamespace(Namespace parent, String next) {
        return new Namespace(parent, JSKeywords.convertPackageName(next));
    }
}
