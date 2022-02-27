package com.warmthdawn.mod.kubejsdtsmaker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class PropertySignature {
    private String name;
    private boolean readonly;
    private Type type;
    private Class<?> originalClass;

    public PropertySignature(String name, boolean readonly, Type type, Class<?> originalClass) {
        this.name = name;
        this.readonly = readonly;
        this.type = type;
        this.originalClass = originalClass;
    }

    public PropertySignature(String name, Field field) {
        this.name = name;
        this.type = field.getGenericType();
        int modifiers = field.getModifiers();
        readonly = Modifier.isFinal(modifiers);
        originalClass = field.getDeclaringClass();
    }

    public String getName() {
        return name;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Class<?> getOriginalClass() {
        return originalClass;
    }

    public PropertySignature withoutReadonly() {
        if (this.readonly) {
            return new PropertySignature(this.name, false, this.type, originalClass);
        }
        return this;
    }

    public Type getType() {
        return type;
    }
}
