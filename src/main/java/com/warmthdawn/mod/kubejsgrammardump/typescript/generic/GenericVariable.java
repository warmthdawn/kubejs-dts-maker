package com.warmthdawn.mod.kubejsgrammardump.typescript.generic;

import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSPrimitive;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GenericVariable implements IPartialType {
    private final IType[] bounds;
    private final String name;

    public GenericVariable(IType[] bounds, String name) {
        this.bounds = bounds;
        this.name = name;
    }

    private static class ResolvedVariable implements IType {
        public ResolvedVariable(String name) {
            this.name = name;
        }

        private String name;

        @Override
        public @Nonnull String getSignature() {
            return name;
        }
    }

    @Override
    public IType resolve(@Nullable GenericVariableProvider provider) {
        if (provider != null && provider.containsVariable(name)) {
            return new ResolvedVariable(this.name);
        }
        return TSPrimitive.ANY;
    }

    public void appendTo(StringBuilder builder, GenericVariableProvider provider) {
        builder.append(resolve(provider).getSignature());
//        int lastIndex = bounds.length - 1;
//        for (int i = 0; i < bounds.length; i++) {
//            if (i == 0) {
//                builder.append(" extends ");
//            }
//            IType variable = bounds[i];
//            builder.append(variable.resolve(provider).getSignature());
//            if (i != lastIndex) {
//                builder.append(" & ");
//            }
//        }
    }

    public String getName() {
        return name;
    }
}
