package com.warmthdawn.mod.kubejsdtsmaker.plugins;

import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.bytecode.ScanResult;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.InterfaceDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public interface IBuilderPlugin {
    default void init(BuilderManager manager) {
    }

    default void onInterfaceBuild(Class<?> javaClazz, InterfaceDeclaration interfaceDeclaration, boolean isStatic) {
    }

    default TypeReference onParameterWrapper(Class<?> type, TypeReference old) {
        return null;
    }

    default void onResolveFinished() {
    }

    default void acceptScanData(ScanResult result) {
    }

    default void addResolveClass(Set<Class<?>> resolveClass) {
    }

    default void addExtraGlobals(List<IGlobalDeclaration> output, List<Namespace> extraNamespaces) {
    }
}
