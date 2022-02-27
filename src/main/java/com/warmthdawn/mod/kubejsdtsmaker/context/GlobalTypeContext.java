package com.warmthdawn.mod.kubejsdtsmaker.context;

import java.util.*;

public class GlobalTypeContext {

    private final Map<String, Class<?>> globalVars = new HashMap<>();
    private final Map<String, Class<?>> typeAliases = new HashMap<>();

    public Map<String, Class<?>> getGlobalVars() {
        return globalVars;
    }

    public Map<String, Class<?>> getTypeAliases() {
        return typeAliases;
    }


    public Set<Class<?>> getReferencedClasses() {
        HashSet<Class<?>> objects = new HashSet<>(globalVars.values());
        objects.addAll(typeAliases.values());
        return objects;
    }

}
