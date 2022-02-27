package com.warmthdawn.mod.kubejsdtsmaker.collector;

import com.warmthdawn.mod.kubejsdtsmaker.context.GlobalTypeContext;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.util.DynamicFunction;

public class WrappedBindingsEvent extends BindingsEvent {
    private ScriptType type;
    private final GlobalTypeContext collector;


    public WrappedBindingsEvent(ScriptManager manager, GlobalTypeContext collector) {
        super(manager, null, null);
        this.collector = collector;
    }

    public static void collectGlobals(GlobalTypeContext collector) {
        for (ScriptType value : ScriptType.values()) {
            ScriptManager manager = new ScriptManager(value, null, null);
            WrappedBindingsEvent event = new WrappedBindingsEvent(manager, collector);
            KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(event));
            BindingsEvent.EVENT.invoker().accept(event);
        }
    }

    @Override
    public ScriptType getType() {
        return type;
    }

    @Override
    public void add(String name, Object value) {
        if (value.getClass() == Class.class) {
            collector.getTypeAliases().put(name, (Class<?>) value);
        } else {
            collector.getGlobalVars().put(name, value.getClass());
        }

    }

    @Override
    @Deprecated
    public void addClass(String name, Class<?> clazz) {
        collector.getTypeAliases().put(name, clazz);
    }

    @Override
    public void addFunction(String name, DynamicFunction.Callback callback) {

    }

    @Override
    public void addFunction(String name, DynamicFunction.Callback callback, Class<?>... types) {

    }

    @Deprecated
    public void addConstant(String name, Object value) {
        collector.getGlobalVars().put(name, value.getClass());
    }

    @Override
    public void addFunction(String name, BaseFunction function) {

    }
}
