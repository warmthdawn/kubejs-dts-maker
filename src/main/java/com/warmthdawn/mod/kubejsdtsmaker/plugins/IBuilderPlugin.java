package com.warmthdawn.mod.kubejsdtsmaker.plugins;

import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;

import java.util.List;
import java.util.Set;

public interface IBuilderPlugin {
    default void init(BuilderManager manager) {
    }

    default void addResolveClass(Set<Class<?>> resolveClass) {
    }

    default void addExtraGlobals(List<IGlobalDeclaration> output) {
    }
}
