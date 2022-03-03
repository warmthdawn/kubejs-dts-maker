package com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;

import java.util.List;

public class ExtrasDeclaration implements IDeclaration {
    private List<String> lines;

    public ExtrasDeclaration(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        for (int i = 0; i < lines.size(); i++) {
            if (i != 0) {
                builder.newLine();
            }
            builder.append(lines.get(i));
        }
    }
}
