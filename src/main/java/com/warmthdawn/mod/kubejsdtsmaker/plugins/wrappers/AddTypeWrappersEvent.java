package com.warmthdawn.mod.kubejsdtsmaker.plugins.wrappers;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.List;
import java.util.Map;

public class AddTypeWrappersEvent extends EventJS {

    @HideFromJS
    public AddTypeWrappersEvent() {

    }

    private Map<Class<?>, String[]> configWrappers;

    @HideFromJS
    public Map<Class<?>, String[]> getConfigWrappers() {
        return configWrappers;
    }

    public void configWrapper(Object clazz, String... configs) {
        if (clazz instanceof Class<?>) {
            configWrappers.put((Class<?>) clazz, configs);
        } else if (clazz instanceof String) {
            String name = (String) clazz;
            try {
                configWrappers.put(Class.forName(name), configs);
            } catch (ClassNotFoundException e) {
                ConsoleJS.SERVER.error("Failed to load class '" + name + "'!", e);
            }
        } else {
            ConsoleJS.SERVER.error("clazz could only be either class or string!");
        }
    }

    public void printAllWrappers() {

    }

    public void printHelpTypes() {

    }


}
