package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import javax.annotation.Nonnull;

public class TSFuncVariantType implements IType {
    private final IType type;

    public TSFuncVariantType(IType type) {
        this.type = type;
    }

    @Override
    public @Nonnull String getSignature() {
        return "..." + type + "[]";
    }
}
