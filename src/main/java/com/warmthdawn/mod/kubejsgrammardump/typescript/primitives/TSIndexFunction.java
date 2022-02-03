package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

/**
 * 表示TypeScript的索引方法
 */
public class TSIndexFunction implements IClassMember, ILineBuilder {
    private IType type;
    private boolean readonly;
    //索引变量名
    private String varName = "index";
    //索引类型，true为number，false为string
    private boolean isNumber = true;

    public TSIndexFunction(IType type, boolean readonly, String varName, boolean isNumber) {
        this.type = type;
        this.readonly = readonly;
        this.varName = varName;
        this.isNumber = isNumber;
    }

    public TSIndexFunction(IType type, boolean readonly) {
        this.type = type;
        this.readonly = readonly;
    }

    @Override
    public void generate(StringBuilder builder) {
        if (readonly) {
            builder.append("readonly ");
        }
        builder.append("[").append(varName);
        if (isNumber) {
            builder.append(": number]: ");
        } else {
            builder.append(": string]: ");
        }
        builder.append(type.getSignature()).append("\n");
    }

    @Override
    public String getName() {
        return isNumber ? "[number]" : "[string]";
    }
}
