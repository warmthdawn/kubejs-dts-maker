package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.ISignatureDeclaration;

public interface TsType extends ISignatureDeclaration {

    default String getSignature() {
        StringBuilder builder = new StringBuilder();
        buildSignature(builder);
        return builder.toString();
    }
}
