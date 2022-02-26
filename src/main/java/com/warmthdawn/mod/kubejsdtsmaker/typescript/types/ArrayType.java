package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

public class ArrayType implements TsType {
    private final TsType elementType;

    public ArrayType(TsType elementType) {
        this.elementType = elementType;
    }

    @Override
    public void buildSignature(StringBuilder builder) {
        elementType.buildSignature(builder);
        builder.append("[]");
    }
}
