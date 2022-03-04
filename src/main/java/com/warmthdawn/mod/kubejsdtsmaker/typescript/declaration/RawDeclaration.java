package com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;

import java.util.List;
import java.util.Objects;

public class RawDeclaration implements IDeclaration {
    private List<String> lines;

    public RawDeclaration(List<String> lines) {
        Objects.requireNonNull(lines);
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
