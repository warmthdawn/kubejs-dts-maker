package com.warmthdawn.mod.kubejsdtsmaker.typescript.global;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

import java.util.Objects;

public class GlobalVariableDeclaration implements IGlobalDeclaration {
    private String name;
    private boolean readonly;
    private TsType type;

    public GlobalVariableDeclaration(String name, boolean readonly, TsType type) {
        Objects.requireNonNull(type);
        this.name = name;
        this.readonly = readonly;
        this.type = type;
    }

    @Override
    public void build(DeclarationBuilder builder) {

        if (readonly) {
            builder.append("declare const ");
        } else {
            builder.append("declare var ");
        }
        builder.append(name)
            .append(": ")
            .append(type)
            .append(";");
    }
}
