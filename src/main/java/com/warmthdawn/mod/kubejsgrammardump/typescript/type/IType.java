package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariableProvider;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.IPartialType;

import javax.annotation.Nonnull;

public interface IType extends IPartialType {

    @Nonnull
    String getSignature();

    default IType resolve(GenericVariableProvider provider) {
        return this;
    }
}
