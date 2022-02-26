package com.warmthdawn.mod.kubejsdtsmaker.context;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.util.MiscUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BuildContext {
    private Map<Class<?>, String> namespaceMappings;
    private Map<Class<?>, IDeclaration> typeMappings;
    private Map<Class<?>, IDeclaration> constructorTypeMappings;

    public BuildContext() {
        namespaceMappings = new HashMap<>();
        typeMappings = new HashMap<>();
        constructorTypeMappings = new HashMap<>();
    }

    private boolean loaded = false;

    public void addNamespace(Class<?> raw, String mapped) {
        namespaceMappings.put(raw, mapped);
    }

    public void addType(Class<?> clazz, IDeclaration type, IDeclaration constructor) {
        typeMappings.put(clazz, type);
        if (constructor != null) {
            constructorTypeMappings.put(clazz, constructor);
        }
    }


    public String getNamespace(Class<?> clazz) {
        return namespaceMappings.get(clazz);
    }


}
