package com.warmthdawn.mod.kubejsdtsmaker.util;

import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class OverrideUtils {

    public static boolean areSignatureSame(MethodSignature source, MethodSignature other) {
        return areParametersSame(source, other) && areReturnSame(source, other);
    }

    public static boolean areSignatureCovariant(MethodSignature source, MethodSignature other) {
        return areParametersCovariant(source, other) && areReturnCovariant(source, other);
    }

    public static boolean areParametersCovariant(MethodSignature source, MethodSignature other) {
        Type[] myPrmTypes = source.getParameterType();
        Type[] otherPrmTypes = other.getParameterType();
        if (myPrmTypes.length != otherPrmTypes.length) return false;
        for (int i = 0; i < myPrmTypes.length; i++) {
            if (!TypeUtils.isAssignable(myPrmTypes[i], otherPrmTypes[i])) return false;
        }
        return true;
    }

    public static boolean areParametersSame(MethodSignature source, MethodSignature other) {
        Type[] myPrmTypes = source.getParameterType();
        Type[] otherPrmTypes = other.getParameterType();
        if (myPrmTypes.length != otherPrmTypes.length) return false;
        for (int i = 0; i < myPrmTypes.length; i++) {
            if (!TypeUtils.equals(myPrmTypes[i], otherPrmTypes[i])) return false;
        }
        return true;
    }


    public static boolean areReturnCovariant(MethodSignature source, MethodSignature other) {
        if (other.getReturnType() == void.class) {
            return true;
        }

        return TypeUtils.isAssignable(source.getReturnType(), other.getReturnType());
    }


    public static boolean areReturnSame(MethodSignature source, MethodSignature other) {
        return TypeUtils.equals(source.getReturnType(), other.getReturnType());
    }
}
