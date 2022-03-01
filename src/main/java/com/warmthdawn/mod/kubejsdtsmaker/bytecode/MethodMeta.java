package com.warmthdawn.mod.kubejsdtsmaker.bytecode;

import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

public class MethodMeta {
    private final String methodName;
    private final String methodDeclaringClass;
    private final String signature;

    public MethodMeta(String methodName, String methodDeclaringClass, String signature) {
        this.methodName = methodName;
        this.methodDeclaringClass = methodDeclaringClass;
        this.signature = signature;
    }

    public static MethodMeta getMeta(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        String name = method.getName();

        return new MethodMeta(
            name,
            Type.getType(declaringClass).getInternalName(),
            Type.getMethodDescriptor(method)
        );
    }

    public static MethodMeta getMeta(Constructor<?> constructor) {
        Class<?> declaringClass = constructor.getDeclaringClass();
        String name = "<init>";

        return new MethodMeta(
            name,
            Type.getType(declaringClass).getInternalName(),
            Type.getConstructorDescriptor(constructor)
        );
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodDeclaringClass() {
        return methodDeclaringClass;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodMeta that = (MethodMeta) o;
        return Objects.equals(methodName, that.methodName) && Objects.equals(methodDeclaringClass, that.methodDeclaringClass) && Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, methodDeclaringClass, signature);
    }
}
