package com.warmthdawn.mod.kubejsdtsmaker.typescript.member;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

public class FieldMember implements Member {
    private String name;
    private boolean readonly;
    private TsType type;

    public FieldMember(String name, boolean readonly, TsType type) {
        this.name = name;
        this.readonly = readonly;
        this.type = type;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        builder.newLine();
        if (readonly) {
            builder.append("readonly ");
        }
        builder.append(name)
            .append(": ")
            .append(type)
            .append(";");
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        if (readonly) {
            return "readonly " + name + ": " + type.getSignature();
        }
        return name + ": " + type.getSignature();
    }
}
