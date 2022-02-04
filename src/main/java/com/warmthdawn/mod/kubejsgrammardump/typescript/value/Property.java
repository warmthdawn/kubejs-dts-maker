package com.warmthdawn.mod.kubejsgrammardump.typescript.value;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.AbstractClass;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import javax.annotation.Nullable;

public class Property extends AbstractValue implements ILineBuilder, IClassMember {


    public Property(String name, IType type, boolean readonly) {
        super(name, type, readonly);
    }

    @Override
    public void generate(StringBuilder builder) {
        if (readonly) {
            builder.append("readonly ");
        }
        builder.append(name).append(": ").append(type.resolve(relevantClass).getSignature()).append(";");
    }

    private AbstractClass relevantClass;

    @Override
    @Nullable
    public AbstractClass getRelevantClass() {
        return relevantClass;
    }

    @Override
    public void setRelevantClass(AbstractClass relevantClass) {
        this.relevantClass = relevantClass;
    }
}