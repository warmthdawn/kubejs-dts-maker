package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

import javax.annotation.Nonnull;

public enum PredefinedTypes implements TsType {
    NEVER("never"),
    UNDEFINED("undefined"),
    VOID("void"),
    UNKNOWN("unknown"),
    SYMBOL("symbol"),
    BOOLEAN("boolean"),
    NUMBER("number"),
    STRING("string"),
    OBJECT("object"),
    ANY("any");

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
