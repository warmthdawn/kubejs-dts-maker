package com.warmthdawn.mod.kubejsgrammardump.typescript.generic;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public interface IPartialType {
    IType resolve(GenericVariableProvider provider);
}
