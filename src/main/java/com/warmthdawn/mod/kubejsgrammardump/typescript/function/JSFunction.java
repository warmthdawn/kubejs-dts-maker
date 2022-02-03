package com.warmthdawn.mod.kubejsgrammardump.typescript.function;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class JSFunction implements IClassMember, ILineBuilder {
    public JSFunction(String name, IType returnType, FunctionParameter[] parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    private final String name;
    private final IType returnType;
    private final FunctionParameter[] parameters;

    @Override
    public void generate(StringBuilder builder) {
        builder.append(name).append("(");
        for (int i = 0; i < parameters.length; i++) {
            parameters[i].appendTo(builder);
            if (i != parameters.length - 1) {
                builder.append(", ");
            }
        }
        builder.append("): ").append(returnType.getSignature()).append(";");

    }

    @Override
    public String getName() {
        return name;
    }
}
