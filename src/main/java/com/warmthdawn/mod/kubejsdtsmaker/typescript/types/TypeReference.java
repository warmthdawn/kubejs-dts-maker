package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeArguments;

public class TypeReference implements TsType {
    private TypeArguments typeArguments;
    private String target;
    private String namespace;

    public TypeReference(TypeArguments typeArguments, String namespace, String target) {
        this.typeArguments = typeArguments;
        this.target = target;
        this.namespace = namespace;
    }

    @Override
    public void buildSignature(StringBuilder builder) {
        //TODO: 判断当前所在命名空间以减少类型引用的长度
        builder.append(namespace)
            .append(".");
        builder.append(target);

        if (typeArguments != null) {
            typeArguments.buildSignature(builder);
        }
    }
}
