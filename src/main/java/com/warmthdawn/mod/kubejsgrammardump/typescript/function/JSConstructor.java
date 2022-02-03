package com.warmthdawn.mod.kubejsgrammardump.typescript.function;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

public class JSConstructor extends JSFunction {

    public JSConstructor(IType returnType, FunctionParameter[] parameters) {
        super("new", returnType, parameters);
    }
}
