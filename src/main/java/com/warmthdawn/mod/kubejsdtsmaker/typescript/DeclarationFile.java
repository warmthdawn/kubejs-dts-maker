package com.warmthdawn.mod.kubejsdtsmaker.typescript;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.TypeAliasDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeclarationFile implements IDeclaration {
    private List<Namespace> namespaces;
    private List<IGlobalDeclaration> globals;
    private List<Namespace> extraNamespaces;

    public DeclarationFile(List<Namespace> namespaces) {
        this.namespaces = namespaces;
        this.extraNamespaces = new ArrayList<>();
        this.globals = new ArrayList<>();
    }

    public void addGlobals(Collection<? extends IGlobalDeclaration> globalDeclarations) {
        globals.addAll(globalDeclarations);
    }

    public void addExtraNamespaces(Collection<? extends Namespace> extraNamespaces) {
        this.extraNamespaces.addAll(extraNamespaces);
    }

    @Override
    public void build(DeclarationBuilder builder) {
        for (IGlobalDeclaration global : globals) {
            builder.newLine()
                .append(global);
        }
        for (Namespace namespace : extraNamespaces) {
            builder.newLine()
                .append("declare ")
                .append(namespace);
        }
        for (Namespace namespace : namespaces) {
            builder.newLine()
                .append("declare ")
                .append(namespace);
        }
    }
}
