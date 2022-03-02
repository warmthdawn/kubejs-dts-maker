package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

public class EmbeddedType implements TsType {
    private final String declaration;

    public EmbeddedType(String declaration) {
        if (declaration.matches("^\\(.+\\)$")) {
            this.declaration = declaration;
        } else {
            this.declaration = "(" + declaration + ")";
        }
    }

    @Override
    public void buildSignature(StringBuilder builder) {

        builder.append(declaration);
    }
}
