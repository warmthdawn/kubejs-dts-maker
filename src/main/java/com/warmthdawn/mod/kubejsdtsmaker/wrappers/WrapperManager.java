package com.warmthdawn.mod.kubejsdtsmaker.wrappers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.warmthdawn.mod.kubejsdtsmaker.collector.WrapperTypeWrappers;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.KubeJSPlugins;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class WrapperManager {
    private static final Logger logger = LogManager.getLogger();
    public static final WrapperManager INSTANCE = new WrapperManager();

    public WrapperManager() {
        WrapperTypeWrappers wrappers = new WrapperTypeWrappers();
        KubeJSPlugins.forEachPlugin(p -> p.addTypeWrappers(ScriptType.SERVER, wrappers));
        Set<Class<?>> classes = wrappers.getWrappers().keySet();
        for (Class<?> clazz : classes) {
            String name = clazz.getSimpleName();
            String actualName = name;
            for (int i = 0; wrapperNames.containsKey(actualName); i++) {
                actualName = name + i;
            }
            wrapperNames.put(actualName, clazz);
        }

    }

    private BiMap<String, Class<?>> wrapperNames = HashBiMap.create();


    public boolean shouldWrapper(Class<?> clazz) {
        return wrapperNames.containsValue(clazz);
    }

    private Map<Class<?>, WrapperBuilder> builders = new HashMap<>();


    public void addWrapper(WrapperBuilder builder) {
        Class<?> clazz = builder.getTargetClass();
        if (shouldWrapper(clazz)) {
            builders.put(clazz, builder);
        } else {
            logger.warn("Class {} did not registered a wrapper! this may be an mistake, ignoring!", clazz.getName());
        }

    }

    public WrapperBuilder builderFor(Class<?> clazz) {
        BiMap<Class<?>, String> inverse = wrapperNames.inverse();
        if (shouldWrapper(clazz)) {
            String s = inverse.get(clazz);
            WrapperBuilder wrapperBuilder = new WrapperBuilder(clazz, s);
            builders.put(clazz, wrapperBuilder);
            return wrapperBuilder;
        } else {
            logger.warn("Class {} did not registered a wrapper! this may be an mistake, ignoring!", clazz.getName());
        }
        return WrapperBuilder.EMPTY;
    }

    public void forEachWrapper(Consumer<WrapperBuilder> builderConsumer) {
        for (WrapperBuilder value : builders.values()) {
            builderConsumer.accept(value);
        }
    }

}
