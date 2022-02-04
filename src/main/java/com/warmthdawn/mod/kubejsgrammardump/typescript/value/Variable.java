package com.warmthdawn.mod.kubejsgrammardump.typescript.value;

import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class Variable extends AbstractValue implements ILineBuilder {
    public Variable(String name, IType type, boolean readonly) {
        super(name, type, readonly);
    }

    @Override
    public void generate(StringBuilder builder) {
        builder.append("declare var ");
        builder.append(name).append(": ").append(type.getSignature()).append(";");
    }
}
