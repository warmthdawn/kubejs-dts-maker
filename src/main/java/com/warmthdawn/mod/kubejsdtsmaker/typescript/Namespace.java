package com.warmthdawn.mod.kubejsdtsmaker.typescript;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;

import java.util.List;

public class Namespace implements IDeclaration {
    private final String identity;
    private final List<IDeclaration> children;

    public Namespace(String identity, List<IDeclaration> children) {
        this.identity = identity;
        this.children = children;
    }

    public String getIdentity() {
        return identity;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        builder.append("namespace ")
            .append(identity)
            .append(" {")
            .increaseIndent();

        for (int i = 0; i < children.size(); i++) {
            if (i != 0) {
                builder.newLine();
            }
            builder.newLine().append("export ").append(children.get(i));
        }

        builder.decreaseIndent()
            .newLine()
            .append("}");
    }

    @Override
    public String toString() {
        return "namespace " + identity;
    }
}
