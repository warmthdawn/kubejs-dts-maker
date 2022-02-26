package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

public class TsTypeVariable implements TsType {
    private String name;

    public TsTypeVariable(String name) {
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
