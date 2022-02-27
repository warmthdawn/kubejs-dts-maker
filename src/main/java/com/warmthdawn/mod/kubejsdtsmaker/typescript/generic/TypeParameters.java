package com.warmthdawn.mod.kubejsdtsmaker.typescript.generic;


import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.ISignatureDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;

import java.util.List;

public class TypeParameters implements ISignatureDeclaration {
    private final List<TypeParameter> parameters;

    public List<TypeParameter> getParameters() {
        return parameters;
    }

    public int paramsSize() {
        return parameters.size();
    }

    public TypeParameters(List<TypeParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void buildSignature(StringBuilder builder) {
        builder.append("<");
        BuilderUtils.join(builder, ", ", parameters, TypeParameter::buildSignature);
        builder.append(">");
    }
}
