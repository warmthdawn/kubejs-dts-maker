package com.warmthdawn.mod.kubejsdtsmaker.typescript.misc;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeArguments;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeParameters;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

import java.util.List;

public class CallSignature implements IDeclaration {
    private List<TsType> paramsTypes;
    private TypeParameters typeParameters;
    public TsType returnType;

    public List<TsType> getParamsTypes() {
        return paramsTypes;
    }

    public CallSignature(List<TsType> paramsTypes, TypeParameters typeParameters, TsType returnType) {
        this.paramsTypes = paramsTypes;
        this.typeParameters = typeParameters;
        this.returnType = returnType;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        if (typeParameters != null) {
            builder.append(typeParameters);
        }
        builder.append("(");
        for (int i = 0; i < paramsTypes.size(); i++) {
            builder.append("arg").append(String.valueOf(i))
                .append(": ")
                .append(paramsTypes.get(i));
            if (i != paramsTypes.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append("): ")
            .append(returnType);
    }


    public void buildType(DeclarationBuilder builder) {
        if (typeParameters != null) {
            builder.append(typeParameters);
        }
        builder.append("(");
        for (int i = 0; i < paramsTypes.size(); i++) {
            builder.append("arg").append(String.valueOf(i))
                .append(": ")
                .append(paramsTypes.get(i));
            if (i != paramsTypes.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(") => ")
            .append(returnType);
    }
}
