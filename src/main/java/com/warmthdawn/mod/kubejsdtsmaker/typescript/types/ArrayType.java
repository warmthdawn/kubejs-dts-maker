package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

public class ArrayType implements TsType {
    private final TsType elementType;
    private final int dimension;

    public TsType getElementType() {
        return elementType;
    }

    public ArrayType(TsType elementType) {
        this.elementType = elementType;
        this.dimension = 1;
    }

    public ArrayType(TsType elementType, int dimension) {
        this.elementType = elementType;
        this.dimension = dimension;
    }

    @Override
    public void buildSignature(StringBuilder builder) {
        elementType.buildSignature(builder);
        for (int i = 0; i < dimension; i++) {
            builder.append("[]");
        }
    }
}
