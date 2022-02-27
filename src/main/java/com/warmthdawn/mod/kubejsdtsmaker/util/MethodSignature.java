package com.warmthdawn.mod.kubejsdtsmaker.util;


import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodSignature {
    private Type returnType;
    private Type[] parameterType;
    private TypeVariable<?>[] variables;
    private Class<?> declaringClass;
    private Method rawMethod;

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
        this.returnType = method.getGenericReturnType();
        Type[] parameterTypes = method.getGenericParameterTypes();
        this.parameterType = new Type[parameterTypes.length];
        for (int i = 0; i < parameterType.length; i++) {
            parameterType[i] = parameterTypes[i];
        }
        this.variables = method.getTypeParameters();
        this.declaringClass = method.getDeclaringClass();
        this.rawMethod = method;
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
        this.rawMethod = method;
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
        this.rawMethod = method.rawMethod;
    }

    public Method getRawMethod() {
        return rawMethod;
    }

    private Set<Method> overrideHierarchy;

    public Set<Method> getOverrideHierarchy() {
        if (overrideHierarchy == null) {
            overrideHierarchy = MethodUtils.getOverrideHierarchy(rawMethod, ClassUtils.Interfaces.INCLUDE);
        }
        return overrideHierarchy;
    }

    public boolean isOverridden(Method method) {
        return getOverrideHierarchy().contains(method);
    }

    public boolean isOverridden(MethodSignature method) {
        return isOverridden(method.rawMethod);
    }


}
