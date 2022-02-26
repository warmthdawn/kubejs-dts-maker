package com.warmthdawn.mod.kubejsdtsmaker.typescript.member;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.misc.CallSignature;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.misc.TsConstructorSignature;

import java.util.List;

public class ConstructorMember implements Member {

    private List<TsConstructorSignature> constructors;

    public ConstructorMember(List<TsConstructorSignature> constructors) {
        this.constructors = constructors;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        for (TsConstructorSignature ctor : constructors) {
            builder.newLine()
                .append(ctor)
                .append(";");
        }
    }

    @Override
    public String getName() {
        return "new";
    }


    @Override
    public String toString() {
        return "new()";
    }
}
