package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;

public class TypeAlias implements IType, ILineBuilder {
    private final IType targetType;
    private final String name;

    public TypeAlias(IType targetType, String name) {
        this.targetType = targetType;
        this.name = name;
    }

    public IType getTargetType() {
        return targetType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSignature() {
        return name;
    }

    @Override
    public void generate(StringBuilder builder) {
        builder.append("declare type ").append(name).append(" = ").append(targetType.getSignature()).append(";");
    }
}
