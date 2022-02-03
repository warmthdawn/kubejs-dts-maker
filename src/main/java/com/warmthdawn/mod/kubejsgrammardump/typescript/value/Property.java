package com.warmthdawn.mod.kubejsgrammardump.typescript.value;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class Property extends AbstractValue implements ILineBuilder, IClassMember {



    public Property(String name, IType type, boolean readonly) {
        super(name, type, readonly);
    }

    @Override
    public void generate(StringBuilder builder) {
        if (readonly) {
            builder.append("readonly ");
        }
        builder.append(name).append(": ").append(type.getSignature()).append(";");
    }
}