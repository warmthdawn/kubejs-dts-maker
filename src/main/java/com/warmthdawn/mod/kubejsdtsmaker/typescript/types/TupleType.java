package com.warmthdawn.mod.kubejsdtsmaker.typescript.types;

import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;

import java.util.Collections;
import java.util.List;

public class TupleType implements TsType {
    private final List<TsType> members;

    public TupleType(List<? extends TsType> members) {
        this.members = Collections.unmodifiableList(members);
    }

    @Override
    public void buildSignature(StringBuilder builder) {
        builder.append("[");
        BuilderUtils.join(builder, ", ", members, TsType::buildSignature);
        builder.append("]");
    }
}
