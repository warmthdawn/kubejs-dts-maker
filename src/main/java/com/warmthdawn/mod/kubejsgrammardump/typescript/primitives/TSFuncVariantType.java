package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class TSFuncVariantType implements IType {
    private final IType type;

    public TSFuncVariantType(IType type) {
        this.type = type;
    }

    @Override
    public String getSignature() {
        return "..." + type + "[]";
    }
}
