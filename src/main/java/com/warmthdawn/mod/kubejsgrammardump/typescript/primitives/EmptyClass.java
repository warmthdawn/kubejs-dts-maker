package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class EmptyClass implements IType {
    public static EmptyClass INSTANCE = new EmptyClass();
    @Override
    public String getSignature() {
        return "{}";
    }
}
