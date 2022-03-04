package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

public class StringLiteral implements TsType {
    private final String literal;

    public StringLiteral(String literal) {
        this.literal = literal;
    }

    @Override
    public void buildSignature(StringBuilder builder) {
        builder.append("\"").append(literal).append("\"");
    }

    private String cache = null;

    @Override
    public String getSignature() {
        if (cache == null) {
            cache = TsType.super.getSignature();
        }
        return cache;
    }
}
