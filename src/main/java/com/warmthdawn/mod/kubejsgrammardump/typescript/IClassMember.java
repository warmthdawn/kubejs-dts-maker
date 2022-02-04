package com.warmthdawn.mod.kubejsgrammardump.typescript;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.AbstractClass;

import javax.annotation.Nullable;

public interface IClassMember extends ILineBuilder {
    String getName();
    @Nullable
    AbstractClass getRelevantClass();
    void setRelevantClass(AbstractClass ownerClass);
}
