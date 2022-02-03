package com.warmthdawn.mod.kubejsgrammardump.typescript.value;

import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import dev.latvian.mods.rhino.SymbolKey;

public class JSSymbolProperty extends Property {
    public JSSymbolProperty(SymbolKey symbol, IType type, boolean readonly) {
        super("[" + symbol.getName() + "]", type, readonly);
    }
}
