package com.warmthdawn.mod.kubejsdtsmaker.typescript;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.TypeAliasDeclaration;

import java.util.ArrayList;
import java.util.List;

public class DeclarationFile implements IDeclaration {
    private List<Namespace> namespaces;
    private List<TypeAliasDeclaration> globalAliases;

    public DeclarationFile(List<Namespace> namespaces) {
        this.namespaces = namespaces;
        this.globalAliases = new ArrayList<>();
    }

    @Override
    public void build(DeclarationBuilder builder) {
        for (TypeAliasDeclaration alias : globalAliases) {
            builder.newLine()
                .append("declare ")
                .append(alias);
        }
        for (Namespace namespace : namespaces) {
            builder.newLine()
                .append("declare ")
                .append(namespace);
        }
    }
}
