package com.warmthdawn.mod.kubejsdtsmaker.plugins.wrappers;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.EmbeddedType;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTypeWrappersEvent extends EventJS {

    @HideFromJS
    public AddTypeWrappersEvent() {
        this.configWrappers = new HashMap<>();
        this.extraTypes = new ArrayList<>();
    }

    private Map<Class<?>, Object[]> configWrappers;
    private List<Tuple<Map<String, Object>, String>> extraTypes;


    @HideFromJS
    public List<Tuple<Map<String, Object>, String>> getExtraTypes() {
        return extraTypes;
    }

    @HideFromJS
    public Map<Class<?>, Object[]> getConfigWrappers() {
        return configWrappers;
    }

    public void configWrapper(Object clazz, Object... configs) {
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


    public void createType(String declaration, Map<String, Object> typeMappings) {
        extraTypes.add(new Tuple<>(typeMappings, declaration));
    }

    public EmbeddedType embed(String declaration) {
        return new EmbeddedType(declaration);
    }

    public void printAllWrappers() {

    }

    public void printHelpTypes() {

    }


}
