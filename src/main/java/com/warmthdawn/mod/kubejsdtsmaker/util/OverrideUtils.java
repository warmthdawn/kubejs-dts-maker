package com.warmthdawn.mod.kubejsdtsmaker.util;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public class OverrideUtils {
    public static boolean areSignatureSame(MethodSignature source, MethodSignature other, Map<TypeVariable<?>, Type> arguments) {
        return areParametersSame(source, other, arguments) && areReturnSame(source, other, arguments);
    }

    public static boolean areParametersSame(MethodSignature source, MethodSignature other, Map<TypeVariable<?>, Type> arguments) {
        Type[] myPrmTypes = source.getParameterType();
        Type[] otherPrmTypes = other.getParameterType();
        if (myPrmTypes.length != otherPrmTypes.length) return false;
        for (int i = 0; i < myPrmTypes.length; i++) {
            Type otherParam = GenericUtils.unrollTypeArguments(arguments, otherPrmTypes[i]);
            Type myParam = GenericUtils.unrollTypeArguments(arguments, myPrmTypes[i]);
            if (!GenericUtils.isSameType(myParam, otherParam)) return false;
        }
        return true;
    }

    public static boolean areReturnSame(MethodSignature source, MethodSignature other, Map<TypeVariable<?>, Type> arguments) {
        return GenericUtils.isSameType(GenericUtils.unrollTypeArguments(arguments, source.getReturnType()),
            GenericUtils.unrollTypeArguments(arguments, other.getReturnType()));
    }

}
