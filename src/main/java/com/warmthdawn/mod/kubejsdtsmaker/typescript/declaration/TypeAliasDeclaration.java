package com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeParameters;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.NamedType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

import javax.annotation.Nonnull;

public class TypeAliasDeclaration implements NamedType {
    private final String identity;
    private final TsType targetType;
    private final TypeParameters typeParameters;

    public TypeAliasDeclaration(String identity, TsType targetType, TypeParameters typeParameters) {
        this.identity = identity;
        this.targetType = targetType;
        this.typeParameters = typeParameters;
    }

    public TsType getTargetType() {
        return targetType;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        builder.append("type ")
            .append(identity);
        if (typeParameters != null) {
            builder.append(typeParameters);
        }
        builder.append(" = ")
            .append(targetType)
            .append(";");
    }

    @Nonnull
    @Override
    public String getIdentity() {
        return identity;
    }


    @Override
    public String toString() {
        return "type " + identity + " = " + targetType.getSignature();
    }
}
