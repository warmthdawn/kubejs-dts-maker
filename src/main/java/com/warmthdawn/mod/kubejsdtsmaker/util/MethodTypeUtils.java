package com.warmthdawn.mod.kubejsdtsmaker.util;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.misc.CallSignature;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.ArrayType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedTypes;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import dev.latvian.mods.rhino.NativeJavaClass;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodTypeUtils {

    public static boolean areSignatureSame(MethodSignature source, MethodSignature other, Class<?> current) {
        Map<TypeVariable<?>, Type> argumentsSource = TypeUtils.getTypeArguments(current, source.getRawMethod().getDeclaringClass());
        Map<TypeVariable<?>, Type> argumentsOther = TypeUtils.getTypeArguments(current, other.getRawMethod().getDeclaringClass());

        return areParametersSame(source, other, argumentsSource, argumentsOther) && areReturnSame(source, other, argumentsSource, argumentsOther);
    }


    public static boolean areParametersSame(MethodSignature source, MethodSignature other, Map<TypeVariable<?>, Type> argumentsSource, Map<TypeVariable<?>, Type> argumentsOther) {
        Type[] myPrmTypes = source.getParameterType();
        Type[] otherPrmTypes = other.getParameterType();
        if (myPrmTypes.length != otherPrmTypes.length) return false;
        for (int i = 0; i < myPrmTypes.length; i++) {
            Type otherParam = GenericUtils.unrollTypeArguments(argumentsOther, otherPrmTypes[i]);
            Type myParam = GenericUtils.unrollTypeArguments(argumentsSource, myPrmTypes[i]);
            if (!GenericUtils.isSameType(myParam, otherParam)) return false;
        }
        return true;
    }

    public static boolean areReturnSame(MethodSignature source, MethodSignature other, Map<TypeVariable<?>, Type> argumentsSource, Map<TypeVariable<?>, Type> argumentsOther) {
        return GenericUtils.isSameType(GenericUtils.unrollTypeArguments(argumentsSource, source.getReturnType()),
            GenericUtils.unrollTypeArguments(argumentsOther, other.getReturnType()));
    }


    public static int compare(CallSignature a, CallSignature b) {
        List<TsType> aTypes = a.getParamsTypes();
        List<TsType> bTypes = b.getParamsTypes();
        int cp = aTypes.size() - bTypes.size();
        if (cp != 0) {
            return cp;
        }

        for (int i = 0; i < aTypes.size(); i++) {
            cp = compareType(aTypes.get(i), bTypes.get(i));
            if (cp != 0) {
                return cp;
            }
        }

        cp = compareType(a.getReturnType(), b.getReturnType());
        if (cp != 0) {
            return -cp;
        }

        int aP = 0;
        if (a.getTypeParameters() != null) {
            aP = a.getTypeParameters().paramsSize();
        }
        int bP = 0;
        if (b.getTypeParameters() != null) {
            bP = b.getTypeParameters().paramsSize();
        }
        cp = aP - bP;
        if (cp != 0) {
            return -cp;
        }

        return 0;
    }

    private static int compareType(Class<?> a, Class<?> b) {
        if (a.isAssignableFrom(b)) {
            return -1;
        }
        if (b.isAssignableFrom(a)) {
            return 1;
        }
        return a.getMethods().length + a.getFields().length - b.getFields().length - b.getMethods().length;
    }


    private static int compareType(TsType a, TsType b) {
        if (a == PredefinedTypes.ANY && b == PredefinedTypes.ANY) {
            return 0;
        }
        if (a == PredefinedTypes.ANY) {
            return 1;
        }
        if (b == PredefinedTypes.ANY) {
            return -1;
        }
        if (a instanceof PredefinedTypes && b instanceof PredefinedTypes) {
            return ((PredefinedTypes) a).ordinal() - ((PredefinedTypes) b).ordinal();
        }
        if (a instanceof PredefinedTypes)
            return -1;
        if (b instanceof PredefinedTypes)
            return 1;

        if (a instanceof ArrayType && b instanceof ArrayType) {
            return compareType(((ArrayType) a).getElementType(), ((ArrayType) b).getElementType());
        }
        if (a instanceof ArrayType)
            return -1;
        if (b instanceof ArrayType)
            return 1;

        return 0;

    }

}
