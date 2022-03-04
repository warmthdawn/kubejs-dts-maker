package com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;

@FunctionalInterface
public interface IDeclaration {
    void build(DeclarationBuilder builder);
}
