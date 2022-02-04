package com.warmthdawn.mod.kubejsgrammardump.typescript.generic;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GenericType implements IPartialType {
    private final IType rawType;
    private final IPartialType[] argumentTypes;

    public GenericType(IType rawType, IPartialType[] arguementTypes) {
        Objects.requireNonNull(rawType);
        this.rawType = rawType;
        this.argumentTypes = arguementTypes;
    }

    @Override
    public IType resolve(GenericVariableProvider provider) {
        IType[] objects = Arrays.stream(argumentTypes).map(it -> it.resolve(provider)).toArray(IType[]::new);
        return new ResolvedGenericType(rawType, objects);
    }
}