package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class TSArray implements IType {
    private final IType elementType;

    public TSArray(IType elementType) {
        this.elementType = elementType;
    }

    @Override
    public String getSignature() {
        return elementType + "[]";
    }

    public IType getElementType() {
        return elementType;
    }
}
