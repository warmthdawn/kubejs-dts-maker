package com.warmthdawn.mod.kubejsdtsmaker.util;

import java.lang.reflect.*;
import java.util.Set;

public class GenericUtils {
    public static void findRelativeClass(Type type, Set<Class<?>> classes) {
        if (type instanceof Class<?>) {
            if (((Class<?>) type).isArray()) {
                classes.add(((Class<?>) type).getComponentType());
            } else {
                classes.add((Class<?>) type);
            }
        } else if (type instanceof GenericArrayType) {
            findRelativeClass(((GenericArrayType) type).getGenericComponentType(), classes);
        } else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            findRelativeClass(rawType, classes);
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                findRelativeClass(actualTypeArgument, classes);
            }
        } else if (type instanceof TypeVariable) {
//            for (Type bound : ((TypeVariable<?>) type).getBounds()) {
//                findRelativeClass(bound, classes);
//            }
        } else if (type instanceof WildcardType) {
            for (Type upperBound : ((WildcardType) type).getUpperBounds()) {
                findRelativeClass(upperBound, classes);
            }
        }
    }
}
