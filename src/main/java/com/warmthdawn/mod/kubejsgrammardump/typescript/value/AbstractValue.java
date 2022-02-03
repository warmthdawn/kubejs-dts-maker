package com.warmthdawn.mod.kubejsgrammardump.typescript.value;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class AbstractValue {
    protected String name;
    protected IType type;
    protected boolean readonly;

    public String getName() {
        return name;
    }

    public IType getType() {
        return type;
    }

    public boolean isReadonly() {
        return readonly;
    }


    public AbstractValue(String name, IType type, boolean readonly) {
        this.name = name;
        this.type = type;
        this.readonly = readonly;
    }
}
