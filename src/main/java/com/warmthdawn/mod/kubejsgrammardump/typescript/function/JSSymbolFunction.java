package com.warmthdawn.mod.kubejsgrammardump.typescript.function;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import dev.latvian.mods.rhino.SymbolKey;

public class JSSymbolFunction extends JSFunction {
    public JSSymbolFunction(SymbolKey symbol, IType returnType, FunctionParameter[] parameters) {
        super("[" + symbol.getName() + "]", returnType, parameters);
    }
}
