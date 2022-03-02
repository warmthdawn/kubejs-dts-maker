package com.warmthdawn.mod.kubejsdtsmaker.typescript.misc;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeParameters;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.member.Member;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public class CallSignature implements IDeclaration {
    private List<TsType> paramsTypes;
    private TypeParameters typeParameters;
    private TsType returnType;
    private List<String> parameterNames;
    private boolean isVarargs = false;

    public List<TsType> getParamsTypes() {
        return paramsTypes;
    }

    public TypeParameters getTypeParameters() {
        return typeParameters;
    }

    public TsType getReturnType() {
        return returnType;
    }

    public CallSignature(List<TsType> paramsTypes, TypeParameters typeParameters, TsType returnType, List<String> parameterNames) {
        this.paramsTypes = paramsTypes;
        this.typeParameters = typeParameters;
        this.returnType = returnType;
        this.parameterNames = parameterNames;
    }

    public boolean isVarargs() {
        return isVarargs;
    }

    public void setVarargs(boolean varargs) {
        isVarargs = varargs;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        buildCommons(builder);
        builder.append("): ")
            .append(returnType);
    }

    public void buildType(DeclarationBuilder builder) {
        buildCommons(builder);
        builder.append(") => ")
            .append(returnType);
    }

    private void buildCommons(DeclarationBuilder builder) {
        if (typeParameters != null) {
            builder.append(typeParameters);
        }
        builder.append("(");
        for (int i = 0; i < paramsTypes.size(); i++) {
            boolean last = (i == paramsTypes.size() - 1);
            if(last && isVarargs) {
                builder.append("...");
            }
            builder.append(getParameterName(i))
                .append(": ")
                .append(paramsTypes.get(i));
            if (!last) {
                builder.append(", ");
            }
        }
    }

    private String getParameterName(int index) {
        if (parameterNames != null && index < parameterNames.size()) {
            String name = parameterNames.get(index);
            if (Strings.isNotEmpty(name)) {
                return name;
            }
        }
        return "arg" + index;
    }
}
