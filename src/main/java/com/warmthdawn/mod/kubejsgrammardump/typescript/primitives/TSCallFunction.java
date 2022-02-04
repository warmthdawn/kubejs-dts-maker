package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.AbstractClass;

import javax.annotation.Nullable;

/**
 * 表示TypeScript的一个可以直接调用的方法
 */
public class TSCallFunction implements IClassMember {
    @Override
    public String getName() {
        return "()";
    }

    @Override
    public void generate(StringBuilder builder) {

    }

    private AbstractClass ownerClass;

    @Override
    @Nullable
    public AbstractClass getRelevantClass() {
        return ownerClass;
    }

    @Override
    public void setRelevantClass(AbstractClass ownerClass) {
        this.ownerClass = ownerClass;
    }
}
