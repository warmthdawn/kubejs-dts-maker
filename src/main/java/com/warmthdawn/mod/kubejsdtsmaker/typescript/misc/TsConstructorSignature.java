package com.warmthdawn.mod.kubejsdtsmaker.typescript.misc;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeParameters;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

import java.util.List;

public class TsConstructorSignature extends CallSignature {
    public TsConstructorSignature(List<TsType> paramsTypes, TypeParameters typeParameters, TsType returnType, List<String> parameterNames) {
        super(paramsTypes, typeParameters, returnType, parameterNames);
    }

    @Override
    public void build(DeclarationBuilder builder) {
        builder.append("new ");
        super.build(builder);
    }
}
