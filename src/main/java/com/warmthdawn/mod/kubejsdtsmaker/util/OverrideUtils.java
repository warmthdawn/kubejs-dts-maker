package com.warmthdawn.mod.kubejsdtsmaker.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class OverrideUtils {
    public boolean areParametersCovariant(Method source, Method other) {
        Class<?>[] myPrmTypes = source.getParameterTypes();
        Class<?>[] otherPrmTypes = other.getParameterTypes();
        if (myPrmTypes.length != otherPrmTypes.length) return false;

        for (int i = 0; i < myPrmTypes.length; i++) {
            if (!(otherPrmTypes[i].isAssignableFrom(myPrmTypes[i]))) return false;
        }
        return true;
    }

    public boolean areParametersSame(Method source, Method other) {
        Class<?>[] myPrmTypes = source.getParameterTypes();
        Class<?>[] otherPrmTypes = other.getParameterTypes();
        if (myPrmTypes.length != otherPrmTypes.length) return false;
        for (int i = 0; i < myPrmTypes.length; i++) {
            if (otherPrmTypes[i] != myPrmTypes[i]) return false;
        }
        return true;
    }


    public boolean areReturnCovariant(Method source, Method other) {
        if (other.getReturnType() == void.class) {
            return true;
        }
        return other.getReturnType().isAssignableFrom(source.getReturnType());
    }


    public boolean areReturnSame(Method source, Method other) {

        return other.getReturnType() == source.getReturnType();
    }
}
