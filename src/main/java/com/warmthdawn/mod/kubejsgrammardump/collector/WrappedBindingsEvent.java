package com.warmthdawn.mod.kubejsgrammardump.collector;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsgrammardump.utils.Utils;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.util.DynamicFunction;
import org.apache.http.cookie.SM;

import java.util.HashMap;
import java.util.Map;

public class WrappedBindingsEvent extends BindingsEvent {
    private ScriptType type;
    private final Map<String, IType> globalVars = new HashMap<>();
    private final Map<String, IType> globalTypes = new HashMap<>();
    private final Multimap<String, JSFunction> globalFuncs = HashMultimap.create();

    public WrappedBindingsEvent(ScriptManager manager) {
        super(manager, null, null);
    }

    public Map<String, IType> getGlobalVars() {
        return globalVars;
    }

    public Map<String, IType> getGlobalTypes() {
        return globalTypes;
    }

    @Override
    public ScriptType getType() {
        return type;
    }

    @Override
    public void add(String name, Object value) {
        if (value.getClass() == Class.class) {
            globalTypes.put(name, Utils.getClassType((Class<?>) value));
        } else {
            globalVars.put(name, Utils.getObjectType(value));
        }

    }

    @Override
    @Deprecated
    public void addClass(String name, Class<?> clazz) {
        globalTypes.put(name, Utils.getClassType(clazz));
    }

    @Override
    public void addFunction(String name, DynamicFunction.Callback callback) {

    }

    @Override
    public void addFunction(String name, DynamicFunction.Callback callback, Class<?>... types) {

    }

    @Deprecated
    public void addConstant(String name, Object value) {
        globalVars.put(name, Utils.getObjectType(value));
    }

    @Override
    public void addFunction(String name, BaseFunction function) {

    }
}
