package com.warmthdawn.mod.kubejsgrammardump.typescript.generic;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.AbstractClass;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IDeclaredType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.LazyType;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ResolvedGenericType implements IType {
    private final IType rawType;
    private final IType[] argumentTypes;

    public ResolvedGenericType(IType rawType, IType[] arguementTypes) {
        Objects.requireNonNull(rawType);
        this.rawType = rawType;
        this.argumentTypes = arguementTypes;
    }

    @Override
    public @Nonnull String getSignature() {
        StringBuilder builder = new StringBuilder()
            .append(rawType.getSignature());

        if (LazyType.isInstance(IDeclaredType.class, rawType) && LazyType.cast(IDeclaredType.class, rawType).isGenericClass()) {
            int lastIndex = argumentTypes.length - 1;
            for (int i = 0; i < argumentTypes.length; i++) {
                if (i == 0) {
                    builder.append("<");
                }
                builder.append(argumentTypes[i].getSignature());
                if (i == lastIndex) {
                    builder.append(">");
                } else {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }
}
