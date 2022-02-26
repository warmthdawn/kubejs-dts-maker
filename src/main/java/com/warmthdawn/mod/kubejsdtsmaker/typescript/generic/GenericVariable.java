package com.warmthdawn.mod.kubejsdtsmaker.typescript.generic;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

public class GenericVariable implements TsType {
    private final String name;

    public GenericVariable(String name) {
        this.name = name;
    }

    @Override
    public void buildSignature(StringBuilder builder) {
        builder.append(name);
    }

    @Override
    public String getSignature() {
        return name;
    }
}
