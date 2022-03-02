package com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;

public class CustomDeclaration implements IDeclaration {
    private String content;

    public CustomDeclaration(String content) {
        this.content = content;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        builder.append(content);
    }
}
