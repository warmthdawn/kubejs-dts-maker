package com.warmthdawn.mod.kubejsdtsmaker.typescript.member;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

public class IndexMember implements Member {
    private String argName;
    private boolean stringIndex;
    private boolean readonly;
    private TsType type;

    public IndexMember(String argName, boolean stringIndex, boolean readonly, TsType type) {
        this.argName = argName;
        this.stringIndex = stringIndex;
        this.readonly = readonly;
        this.type = type;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        builder.newLine();
        if (readonly) {
            builder.append("readonly ");
        }

        builder.append("[")
            .append(argName)
            .append(": ");
        if (stringIndex) {
            builder.append("string");
        } else {
            builder.append("number");
        }
        builder.append("]")
            .append(": ")
            .append(type)
            .append(";");
    }

    @Override
    public String getName() {
        if (stringIndex) {
            return "[string]";
        }
        return "[number]";
    }


    @Override
    public String toString() {
        if (readonly) {
            return "readonly " + getName() + ": " + type.getSignature();
        }
        return getName() + ": " + type.getSignature();
    }
}
