package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;

import javax.annotation.Nonnull;

public interface NamedType extends IDeclaration {
    @Nonnull
    String getIdentity();
}
