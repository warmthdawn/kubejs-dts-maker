package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

import javax.annotation.Nonnull;

public enum PredefinedTypes implements TsType {
    NEVER("never"),
    ANY("any"),
    UNKNOWN("unknown"),
    VOID("void"),
    UNDEFINED("undefined"),
    NUMBER("number"),
    STRING("string"),
    BOOLEAN("boolean"),
    SYMBOL("symbol"),
    OBJECT("object");

    PredefinedTypes(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public void buildSignature(StringBuilder builder) {
        builder.append(name);
    }

    @Override
    @Nonnull
    public String getSignature() {
        return name;
    }
}
