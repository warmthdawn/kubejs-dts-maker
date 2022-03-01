package com.warmthdawn.mod.kubejsdtsmaker.collector;

import dev.latvian.mods.rhino.util.wrap.TypeWrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class WrapperTypeWrappers extends TypeWrappers {

    private final Map<Class<?>, TypeWrapperFactory<?>> wrappers = new LinkedHashMap<>();

    public Map<Class<?>, TypeWrapperFactory<?>> getWrappers() {
        return wrappers;
    }

    public WrapperTypeWrappers() {

    }

    @Override
    public void removeAll() {
        wrappers.clear();
    }

    @Override
    public <T> void register(Class<T> target, Predicate<Object> validator, TypeWrapperFactory<T> factory) {

        wrappers.put(target, factory);
    }

    @Override
    public <T> void register(Class<T> target, TypeWrapperFactory<T> factory) {
        wrappers.put(target, factory);
    }

    @Override
    public @Nullable TypeWrapperFactory<?> getWrapperFactory(Class<?> target, @Nullable Object from) {
        return null;
    }
}
