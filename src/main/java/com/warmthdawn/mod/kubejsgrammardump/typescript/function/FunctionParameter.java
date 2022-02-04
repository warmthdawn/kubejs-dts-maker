package com.warmthdawn.mod.kubejsgrammardump.typescript.function;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class FunctionParameter {
    private final IType type;
    private final String name;
    private final boolean isVarargs;

    public FunctionParameter(IType type, String name, boolean isVarargs) {
        this.type = type;
        this.name = name;
        this.isVarargs = isVarargs;
    }

    public void appendTo(StringBuilder builder, JSFunction jsFunction) {
        if (isVarargs) {
            builder.append("...");
        }
        builder.append(name).append(": ");
        if (jsFunction != null) {
            builder.append(type.resolve(jsFunction).getSignature());
        } else {
            builder.append(type.getSignature());
        }
    }
}
