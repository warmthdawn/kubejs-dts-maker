package com.warmthdawn.mod.kubejsdtsmaker.util;


import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

public class MethodSignature {
    private Type returnType;
    private Type[] parameterType;
    private TypeVariable<?>[] variables;
    private Class<?> declaringClass;

    public Type getReturnType() {
        return returnType;
    }

    public Type[] getParameterType() {
        return parameterType;
    }

    public TypeVariable<?>[] getVariables() {
        return variables;
    }

    public MethodSignature(Method method) {
        this.returnType = GenericUtils.unrollTypeArguments(null, method.getGenericReturnType());
        Type[] parameterTypes = method.getGenericParameterTypes();
        this.parameterType = new Type[parameterTypes.length];
        for (int i = 0; i < parameterType.length; i++) {
            parameterType[i] = GenericUtils.unrollTypeArguments(null, parameterTypes[i]);
        }
        this.variables = method.getTypeParameters();
        this.declaringClass = method.getDeclaringClass();
    }

    public MethodSignature(Method method, Class<?> type) {
        Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, method.getDeclaringClass());

        this.returnType = GenericUtils.unrollTypeArguments(typeArguments, method.getGenericReturnType());
        Type[] parameterTypes = method.getGenericParameterTypes();

        this.parameterType = new Type[parameterTypes.length];
        for (int i = 0; i < parameterType.length; i++) {
            parameterType[i] = GenericUtils.unrollTypeArguments(typeArguments, parameterTypes[i]);
        }
        this.variables = method.getTypeParameters();
        this.declaringClass = method.getDeclaringClass();
    }

    public MethodSignature(MethodSignature method, Class<?> type) {
        Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, method.declaringClass);

        this.returnType = GenericUtils.unrollTypeArguments(typeArguments, method.getReturnType());
        Type[] parameterTypes = method.getParameterType();

        this.parameterType = new Type[parameterTypes.length];
        for (int i = 0; i < parameterType.length; i++) {
            parameterType[i] = GenericUtils.unrollTypeArguments(typeArguments, parameterTypes[i]);
        }
        this.variables = method.getVariables();
        this.declaringClass = method.declaringClass;
    }

}
