package com.warmthdawn.mod.kubejsdtsmaker.typescript.generic;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.ISignatureDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;


public class TypeParameter implements ISignatureDeclaration {
    public TypeParameter(String identifier, TsType constraintType) {
        this.identifier = identifier;
        this.constraintType = constraintType;
    }

    public TypeParameter(String identifier) {
        this.identifier = identifier;
    }

    private final String identifier;
    private TsType constraintType;

    @Override
    public void buildSignature(StringBuilder builder) {
        builder.append(identifier);
        if (constraintType != null) {
            builder.append(" extends ");
            constraintType.buildSignature(builder);
        }
    }
}
