package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import javax.annotation.Nonnull;

public class TSArray implements IType {
    private final IType elementType;

    public TSArray(IType elementType) {
        this.elementType = elementType;
    }

    @Override
    public @Nonnull String getSignature() {
        return elementType.getSignature() + "[]";
    }

    public IType getElementType() {
        return elementType;
    }
}
