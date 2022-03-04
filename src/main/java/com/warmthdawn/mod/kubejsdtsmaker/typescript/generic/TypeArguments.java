package com.warmthdawn.mod.kubejsdtsmaker.typescript.generic;

import com.google.common.collect.Lists;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.ISignatureDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TypeArguments implements ISignatureDeclaration {
    private final List<TsType> arguments;

    public TypeArguments(List<TsType> arguments) {
        this.arguments = arguments;
    }
    public TypeArguments(TsType ...arguments) {
        this.arguments = Arrays.asList(arguments);
    }

    @Override
    public void buildSignature(StringBuilder builder) {
        builder.append("<");
        BuilderUtils.join(builder, ", ", arguments, ISignatureDeclaration::buildSignature);
        builder.append(">");
    }
}
