package com.warmthdawn.mod.kubejsgrammardump.typescript.generic;

import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSArray;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class GenericTSArray implements IPartialType {
    private final IPartialType elementType;

    public GenericTSArray(IPartialType elementType) {
        this.elementType = elementType;
    }

    @Override
    public IType resolve(GenericVariableProvider provider) {
        return new TSArray(elementType.resolve(provider));
    }
}
