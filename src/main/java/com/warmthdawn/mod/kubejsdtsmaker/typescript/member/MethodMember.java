package com.warmthdawn.mod.kubejsdtsmaker.typescript.member;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.misc.CallSignature;

import java.util.List;

public class MethodMember implements Member {
    private String name;
    private List<CallSignature> methods;

    public MethodMember(String name, List<CallSignature> methods) {
        this.name = name;
        this.methods = methods;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        for (CallSignature method : methods) {
            builder.newLine()
                .append(name)
                .append(method)
                .append(";");
        }
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name + "()";
    }
}
