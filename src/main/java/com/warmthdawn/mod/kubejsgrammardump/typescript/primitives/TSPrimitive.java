package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import javax.annotation.Nonnull;

public enum TSPrimitive implements IType {
    NEVER("never"),
    ANY("any"),
    UNKNOWN("unknown"),
    VOID("void"),
    UNDEFINED("undefined"),
    NUMBER("number"),
    STRING("string"),
    BOOLEAN("boolean"),
    SYMBOL("symbol")
    ;

    TSPrimitive(String name) {
        this.name = name;
    }

    private final String name;
    @Override
    public @Nonnull String getSignature() {
        return name;
    }
}
