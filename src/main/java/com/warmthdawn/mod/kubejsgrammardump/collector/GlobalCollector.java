package com.warmthdawn.mod.kubejsgrammardump.collector;

import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.TypeAlias;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Variable;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.KubeJSPlugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlobalCollector {
    public static GlobalCollector INSTANCE = new GlobalCollector();

    private final List<Variable> globalVars = new ArrayList<>();
    private final List<TypeAlias> typeAliases = new ArrayList<>();

    public List<Variable> getGlobalVars() {
        return globalVars;
    }

    public List<TypeAlias> getTypeAliases() {
        return typeAliases;
    }

    public void collectGlobals() {
        ScriptManager manager = new ScriptManager(ScriptType.STARTUP, null, null);
        WrappedBindingsEvent event = new WrappedBindingsEvent(manager);
        KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(event));
        BindingsEvent.EVENT.invoker().accept(event);

        for (Map.Entry<String, IType> entry : event.getGlobalVars().entrySet()) {
            Variable variable = new Variable(entry.getKey(), entry.getValue(), true);
            globalVars.add(variable);
        }
        for (Map.Entry<String, IType> entry : event.getGlobalTypes().entrySet()) {
            TypeAlias alias = new TypeAlias(entry.getValue(), entry.getKey());
            typeAliases.add(alias);
        }
    }
}
