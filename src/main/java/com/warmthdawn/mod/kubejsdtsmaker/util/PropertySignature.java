package com.warmthdawn.mod.kubejsdtsmaker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class PropertySignature {
    private String name;
    private boolean readonly;
    private Type type;

    public PropertySignature(String name, boolean readonly, Type type) {
        this.name = name;
        this.readonly = readonly;
        this.type = type;
    }

    public PropertySignature(String name, Field field) {
        this.name = name;
        this.type = field.getGenericType();
        int modifiers = field.getModifiers();
        readonly = Modifier.isFinal(modifiers);
    }

    public String getName() {
        return name;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Type getType() {
        return type;
    }
}
