package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import javax.annotation.Nonnull;

public class EmptyClass implements IType {
    public static EmptyClass INSTANCE = new EmptyClass();
    @Override
    public @Nonnull String getSignature() {
        return "{}";
    }
}
